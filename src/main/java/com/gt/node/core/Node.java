package com.gt.node.core;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.*;

import com.gt.beans.Measurement;
import com.gt.beans.NodeBean;

import com.gt.grpc.RingServiceGrpc;
import com.gt.grpc.RingServiceOuterClass;

import com.gt.grpc.TokenServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


/*
Token manager and grpc services share a Node instance. Methods to get/set next/prev and token are synchronized
on the shared Node instance
 */
public class Node {

    /****
     * FIELDS
     */
    /*
       Web target for REST client
    */
    private WebTarget target;

    /*
    These field contains node identity: id, ip, port
     */
    private NodeBean identity;
    /*
    Identities of next and previous node in the ring topology
     */
    private NodeBean prev;
    private NodeBean next;
    /*
    REST server target
     */
    private List<NodeBean> nodesList;
    /*
    Current token: the token manager will wait on node until a new token is delivered to the from the token
    middleware service
    */
    private Token token;
    /*
    This field is used to determine the status of the node. Comparing the id of lastToken and token fields
    the TokenManager can take an action
    */
    private Token lastToken;
    /*
    This field is used to indicate that the node has began the quitting phase
     */
   private boolean quit;
    /*
   This field is used to indicate that the node has notified neighbors of its leaving but still hasn't
   told it to the gateway
    */
   private boolean zombie;
   /*
   Flag to indicate that the node hasn't seen any token yet
    */
   private boolean tokenSeen;

    public Node(String url){
        /*
        Initializing node with next and prev as empty NodeBean with id=-1
         */
        prev=new NodeBean();
        prev.setId(-1);
        next=new NodeBean();
        next.setId(-1);
        nodesList=null;
        target=ClientBuilder.newClient().target(url);
        /*
        Initializing with token=lastToken with id=-1. Start state
         */
        token=Token.newBuilder().setRound(false).build();
        lastToken=token;
        quit=false;
        zombie=false;
        tokenSeen =false;
    }

    /****
     *
     * METHODS
     */

    public NodeBean getIdentity(){return identity;}
    public int getId(){return getIdentity().getId();}
    public int getPort(){return getIdentity().getPort();}
    public InetAddress getIp(){return getIdentity().getIp();}
    public WebTarget getTarget(){return target;}
    public List<NodeBean> getNodesList(){return nodesList;}
    /*
    Quit and zombie field can be accessed by multiple threads so setters must be synchronized
     */
    public synchronized void setQuit(){quit=true;}
    public synchronized void unsetQuit(){quit=false;}
    /*
    This method is called in only one thread so there's no need to synchronize it
     */
    public void setZombie(){zombie=true;}
    public synchronized boolean isZombie(){return zombie;}
    public boolean hasSeenToken(){return tokenSeen;}
    public void setTokenSeen(){tokenSeen=true;}

    /*
    May be accessed by multiple threads
     */
    public synchronized NodeBean getPrev(){return prev;}
    public synchronized NodeBean getNext(){return next;}
    /*
    This methods can be called to get/set a new token by consumer/producer (TokenManager/TokenGrpcService)
     */
    public synchronized Token getNewToken(){
        while(token==lastToken){
            try{
                System.out.println("Thread must wait for a new token to arrive");
                wait();
            }
            catch(InterruptedException e){
                System.out.println("Thread was teared down while waiting in getNextToken method");
            }
        }
        return token;
    }

    /***
    Set new token and notify to TokenManager
     */
    public synchronized void setNewToken(Token t){
        token=t;
        notifyAll();
    }

    public Token getLastToken(){return lastToken;}
    public void setLastToken(Token t){lastToken=t;}
    /***
    Method to register a node identity to the gateway
     */
    public void register(int id,String ip,int port)throws UnknownHostException{

        NodeBean m=new NodeBean(id,InetAddress.getByName(ip),port);
        List<NodeBean> nl=null;
        Response r;
        Random n=new Random();
        boolean retry=false;

        do{
            /*
            If the server response was 400, attempt with another id
             */
            if(retry)
                m.setId(n.nextInt(Integer.MAX_VALUE));

            retry=false;

            r=getTarget().path("nodes/login").request(MediaType.APPLICATION_XML).
                        post(Entity.entity(m,MediaType.APPLICATION_XML));
            /*
              If the server responds 400 and ip and port are valid, id must be changed
             */
            if(r.getStatus()==400)
                retry=true;
            /*
               Else get the node map
             */
            else
                nl=r.readEntity(new GenericType<List<NodeBean>>(){});

        }while(retry);

        nodesList=nl;
        identity=m;

    }

    /***
     Method to register a node identity to the gateway
     */
    public void remove(){
        Response r;
        r = target.path("nodes/"+getId()).request(MediaType.APPLICATION_XML).
                delete();
        System.out.println("Node has left the the network");
    }

    /****
     Compute the mean of a list of measurements an post to the gateway
    */
    public void updateGateway(List<MeasurementProto> l){
        double mean=0;
        Response r=null;
        /*
         Compute the mean of the measurements in the list
        */
        for(int i=0;i<l.size();i++)
            mean+=l.get(i).getValue();

        mean/=l.size();

        /*
        Create a measurement with the id of sending node
         */
        Measurement m=new Measurement(Integer.toString(getId()),l.get(0).getType(),
                mean,System.currentTimeMillis());

        try{
            r=getTarget().path("nodes/add").request(MediaType.APPLICATION_XML).
                    post(Entity.entity(m,MediaType.APPLICATION_XML));
            System.out.println("Gateway replied "+r.getStatus());
        }
        catch(ProcessingException e){
            e.printStackTrace();
        }
    }


    /***
     * The following methods are the ones that guarantee consistent update of next and prev
     * pointers. That idea is that if an update is consistent then it will be permitted otherwise
     * it will be treated as an old update that arrived late and it will be discarded.
     * True if the update is valid, false otherwise.
     * Methods are synchronized on the Node to avoid inconsistency given by concurrent updates.
     */
    /*
       Token goes from the largest id node to the smallest in the ring. So a consistent new
        next/prev must be in between the node id and the id of the old one
    */
    public synchronized boolean enterNext(NodeBean n){
        /*
        If the node is completing exit protocol, wait
         */
        while(quit){
            try {
                wait();
            }
            catch(InterruptedException e){
                System.out.println("Thread was teared down while waiting for the node to complete" +
                        " exit procedure");
            }
            return false;
        }
        /*
        If the node is in zombie phase return false
         */
        if(isZombie())
            return false;
        /*
        If no next has been already found or if i'm the next of myself
         */
        if(next.getId()==-1) {
            next = n;
            /*
            Notify to TokenManager if it's waiting for the node to have found a next
             */
            notifyAll();
        }

        /*
        If a node is next of itself, then the new node will be the its next and prev
         */
        else if(next.equals(getIdentity())){
            next=n;
            prev=n;
        }

        /*
        Else must be in between the node and its current next
         */
        else if(next.getId()>getId()){
            if(n.getId()<getId() || next.getId()<=n.getId())
                next=n;
            else
                return false;
        }

        else{
            if(next.getId()<=n.getId() && n.getId()<getId())
                next=n;
            else
                return false;
        }
       logNextPrev();
        return true;
    }

    public synchronized boolean enterPrev(NodeBean n){
        /*
        If the node is completing exit protocol, wait
         */
        while(quit){
            try {
                wait();
            }
            catch(InterruptedException e){
                System.out.println("Thread was teared down while waiting for the node to complete" +
                        " exit procedure");
            }
            return false;
        }
         /*
        If the node is in zombie phase return false
         */
        if(isZombie())
            return false;
       /*
        If no prev has been already found
         */
        if(prev.getId()==-1)
            prev = n;

        /*
        If a node is prev of itself, then the new node will be the its next and prev
         */
        else if(prev.equals(getIdentity())){
            next=n;
            prev=n;
        }

        else if(prev.getId()<getId()){
            if(n.getId()>getId() || prev.getId()>=n.getId())
                prev=n;
            else
                return false;
        }

        else{
            if(getId()<n.getId() && n.getId()<=prev.getId())
                prev=n;
            else
                return false;
        }
        logNextPrev();
        return true;
    }

    /*
        A removal of next/prev is to be considered consistent if the sender is effectively the next/prev of the node.
        Otherwise the sender has missed an update in the topology and it's request is to be discarded
     */
    public synchronized boolean quitNext(NodeBean sender, NodeBean n){
        if(next.getId()==-1 || sender.getId()==next.getId()){
            next=n;
            logNextPrev();
            return true;
        }
        else
            return false;
    }

    public synchronized boolean quitPrev(NodeBean sender, NodeBean n){
        if(prev.getId()==-1 || sender.getId()==prev.getId()){
            prev=n;
            logNextPrev();
            return true;
        }
        else
            return false;
    }

    private void logNextPrev(){
        System.out.println("********************");
        System.out.println("I'm "+getId()+" and my prev is "+getPrev().getId());
        System.out.println("I'm "+getId()+" and my next is "+getNext().getId());
    }

    public void enter(){
        findNeighbor(true);
        findNeighbor(false);
    }

    /*****
     *Procedure to discover a neighbor while entering the network. The same procedure is used to find
     * prev/next by sorting the nodesList in ascending/descending order
     * @param findPrev : discover prev/next
     */
    private void findNeighbor(boolean findPrev){
        int nodeIdx=0,inactiveCount=0,i=1;
        ManagedChannel channel;
        RingServiceGrpc.RingServiceBlockingStub stub;
        RingServiceOuterClass.RingServiceResponse response=null;
        RingServiceOuterClass.NodeProto sender;
        NodeBean candidate=null;
        RingServiceOuterClass.Update up;
        String t;
        /*
        Used to flag that the node's list isn't
         */
        boolean redirection=false;

        if(findPrev) {
            Collections.sort(nodesList);
            System.out.println("Searching for previous");
        }
        else {
            Collections.sort(nodesList, Collections.<NodeBean>reverseOrder());
            System.out.println("Searching for next");
        }

        System.out.println(findPrev);
        System.out.println(this.getIdentity());
        for(NodeBean a: nodesList)
            System.out.println(a);

        nodeIdx=indexOf(nodesList,this.getIdentity());
        System.out.println(nodeIdx);

        /*
        Sender field in the Update message
         */
        sender= RingServiceOuterClass.NodeProto.newBuilder().setId(this.getId()).setPort(this.getPort()).
                setIp(this.getIp().getHostName()).build();
        /*
         Probe for every node in the list or every till a prev is found
         */
        while((i<=nodesList.size() || redirection)){
            t="";
            try{
                /*
                If not in recovery mode, try the next on the list
                 */
                if(!redirection)
                    candidate=nodesList.get((nodeIdx+i)%nodesList.size());
                /*
                Else try to contact the node indicated as neighbor in the latest response
                 */
                else
                    candidate=new NodeBean(response.getNeighbor().getId(),
                            InetAddress.getByName(response.getNeighbor().getIp()),
                            response.getNeighbor().getPort());

                t+=candidate.getIp().getHostAddress()+":"+
                        candidate.getPort();

                if(redirection)
                    System.out.println("Recovery "+ (findPrev ? "prev":"next") + candidate+
                            " i'm "+this.getIdentity());
                System.out.println(t);

                /*
                Try to create the channel with the candidate
                 */
                channel= ManagedChannelBuilder.forTarget(t).usePlaintext(true).build();
                stub= RingServiceGrpc.newBlockingStub(channel);
                up= RingServiceOuterClass.Update.newBuilder().setSender(sender).build();
                /*
                Call the grpc service of candidate prev/next
                 */
                response=findPrev ? stub.enterNext(up) : stub.enterPrev(up);
                channel.shutdown();
                /*
                If the node probed reply Ok, set him as prev and break
                 */
                if(response.getStatus()==0) {
                    if (findPrev) {
                        this.enterPrev(candidate);
                        System.out.println("Previous discovered");
                    }
                    else {
                        this.enterNext(candidate);
                        System.out.println("Next discovered");
                    }
                    /*
                    Spawn a new token if at the end of the procedure the node has found to be
                    the only active node in the list given by server
                     */
                    if (nodesList.size()-inactiveCount == 1 && findPrev){
                        this.setNewToken(Token.newBuilder().setRound(true).setParticipants(0).
                                addAllMeasurements(new ArrayList<MeasurementProto>()).build());

                        System.out.println("************************");
                        System.out.println("Node " + this.getId() + " spawned new token");
                    }

                    break;
                }
                /*
                The node is redirected to another prev/next because has
                 */
                else if(response.getStatus()==1)
                    redirection=true;
                /*
                The receiver was a zombie node: is simply treated as inactive node
                 */
                else{
                    redirection=false;
                    inactiveCount++;
                    i++;

                }
            }

            catch(StatusRuntimeException e){
                System.out.println("Node "+candidate+" failed to reply");
                /*
                If it was in recovery mode and the node indicated in the latest response doesn't reply,
                */
                redirection=false;
                inactiveCount++;
                i++;
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    /*****
     *Procedure to notify the prev while quitting the network. The node will be unlinked after the procedure
     */
    public void unlink(){
        ManagedChannel channel;
        RingServiceGrpc.RingServiceBlockingStub stub;
        RingServiceOuterClass.RingServiceResponse response=null;
        RingServiceOuterClass.NodeProto sender,m;
        NodeBean neighbor,update;
        RingServiceOuterClass.Update up;
        String t;

         /*
         Sender field of the update message
         */
        sender= RingServiceOuterClass.NodeProto.newBuilder().setId(this.getId()).setPort(this.getPort()).
                setIp(this.getIp().getHostName()).build();

        update=this.getNext();

        while(true){
            t="";
            /*
            Update field of the Update message
            */
            m= RingServiceOuterClass.NodeProto.newBuilder().setId(update.getId()).setIp(update.getIp().getHostName()).
                    setPort(update.getPort()).build();

            try{
                if(response!=null)
                    neighbor=new NodeBean(response.getNeighbor().getId(),
                            InetAddress.getByName(response.getNeighbor().getIp()),
                            response.getNeighbor().getPort());
                else{

                    neighbor=this.getPrev();

                }
                System.out.println(this.getIdentity() +" notifies quit"+ ("Next")
                        +" to "+ neighbor);
                System.out.println(t);
                t+=neighbor.getIp().getHostAddress()+":"+
                        neighbor.getPort();
                /*
                Create the grpc channel with the candidate
                 */
                channel= ManagedChannelBuilder.forTarget(t).usePlaintext(true).build();
                stub= RingServiceGrpc.newBlockingStub(channel);
                up= RingServiceOuterClass.Update.newBuilder().setSender(sender).setUpdate(m).build();
                response=stub.quitNext(up);
                channel.shutdown();
                /*
                If the node probed reply Ok, break procedure
                 */
                if(response.getStatus()==0)
                        break;
            }
            /*
            If the node doesn't reply at all
             */
            catch(StatusRuntimeException e){
                e.printStackTrace();
                break;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //just utility function (had problems with list index of). Fix this if have time
    public static int indexOf(List<NodeBean> l,NodeBean n) {
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).getId() == n.getId())
                return i;
        }
        return -1;
    }
}
