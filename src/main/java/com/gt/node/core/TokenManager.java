package com.gt.node.core;

import com.gt.beans.Measurement;
import com.gt.grpc.GrpcServerResponseOuterClass.*;
import com.gt.grpc.RingServiceOuterClass.*;
import com.gt.grpc.TokenServiceGrpc;
import com.gt.grpc.TokenServiceOuterClass.*;
import com.gt.node.simulator.OutputQueue;

import com.gt.beans.NodeBean;

import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.ManagedChannel;
import com.gt.grpc.TokenServiceGrpc.TokenServiceBlockingStub;

import java.util.ArrayList;
import java.util.List;

/*
Token manager is the thread responsible of taking an action every time a new token arrives.
It's responsible to perform the exit procedure too.
 */
public class TokenManager extends Thread {
    /*
        Node quitting flag
     */
    private volatile boolean quit;
    /*
    Reference to the node of which the thread is the token manager
     */
    private Node node;

    public TokenManager(Node n){
        quit =false;
        node=n;

    }

    /***
     * Main loop function
     */
    public void run(){
        int participants=0;
        boolean currentRound;
        Measurement m;
        List<MeasurementProto> l;
        NodeBean n;
        Token token,lastToken,nextToken;
        /*
        Flag that indicates that the node must insert a new value in the list of measurements
        of the token.
         */
        boolean insertNew;

        while(true){
            insertNew=true;
            /*
            Thread will wait on node for a new token: grpc service will wake it up
            */
            token=node.getNewToken();
            lastToken=node.getLastToken();

            logToken(token);
            /*
            If the id of the current token is different from the previous, this is a new measurement round and
            participants counter must be incremented.
             */
            currentRound=token.getRound();
            participants=token.getParticipants();
            /*
            token.getMeasurementsList returns immutable collection, take a copy to add
             */
            l=new ArrayList<MeasurementProto>(token.getMeasurementsList());
            /*
            Checking if the node has already put a measurement in the list of the current token
             */
            for(MeasurementProto e: l) {
                if (e.getId() == node.getId()) {
                    insertNew = false;
                    break;
                }
            }

            /*
            KEY POINT: if a node received a token with a new id, it's relative to a new measurement round,
            so it will increment the counter of participants
             */
            if(!node.hasSeenToken()){
                participants++;
                node.setTokenSeen();
            }
            else if(currentRound!= lastToken.getRound())
                participants++;

            if(insertNew){
                m= OutputQueue.getInstance().consumeMeasurement();
                /*
                If the sensor has produced a new value in the OutputQueue
                 */
                if(m!=null) {
                    l.add(MeasurementProto.newBuilder().setId(node.getId()).
                            setType(m.getType()).setTimestamp(System.currentTimeMillis()).
                            setValue(m.getValue()).build());
                    insertNew = false;
                }

            }

            /***
             * Condition to empty the token and pass the measure to the gateway. This can be true
             * if the token has completed at least a complete loop in the network
             */
            if(lastToken.getRound()==currentRound && token.getParticipants()==l.size()) {
                node.updateGateway(l);
                /*
                Empty the token
                 */
                l=new ArrayList<MeasurementProto>();
                participants=0;
                /*
                Flip the id (0-1) of the token to pass a token relative to a new measurement round
                 */
                currentRound=!currentRound;
            }
            /*
            If the command to start the exit procedure was given, break the cycle before passing token
             */
            if(quit)
                break;

            nextToken=Token.newBuilder().setRound(currentRound).setParticipants(participants).
                    addAllMeasurements(l).setExit(false).build();

            passOn(nextToken);
             /*
             Set lastToken as token so the TokenManager will wait on node until a new token is delivered from
             the producer
            */
            node.setLastToken(token);


        }
        passExitToken(currentRound,insertNew,participants,l);
        System.out.println("TokenManager stopped");

    }

    public void setQuit(){ quit =true;}

    private void passExitToken(boolean currentRound,boolean insertNew,int participants,List<MeasurementProto> l){
            /*
        Enter a critical region: new entry nodes will wait till unsetQuit
         */
        node.setQuit();
        //try{Thread.sleep(15000);}catch(InterruptedException e){e.printStackTrace();}
        /*
        After exiting the main loop of measurements, decrement the counter of participants if necessary
        ( if the node hasn't already put a measure but it incremented the participant counter at the
        round before ).
         */
        if(insertNew)
            participants--;
        /*
        Unlink from the network telling to prev who will be its next
         */
        node.unlink();
        /*
        Create exit token and pass
         */
        NodeProto sender=NodeProto.newBuilder().setId(node.getId()).setIp(node.getIp().getHostName()).
                setPort(node.getPort()).build();
        NodeProto update=NodeProto.newBuilder().setId(node.getPrev().getId()).
                setIp(node.getPrev().getIp().getHostName()).
                setPort(node.getPrev().getPort()).build();
        Update up=Update.newBuilder().setSender(sender).setUpdate(update).build();
        Token nextToken=Token.newBuilder().setRound(currentRound).setParticipants(participants).
                addAllMeasurements(l).setExit(true).setUpdate(up).build();


        /*
        If the node isn't the last node in the network
         */
        if(node.getNext().getId()!=node.getId())
            passOn(nextToken);

        /*
        Tell the gateway to delete the node
         */
        node.remove();
        node.setZombie();
        node.unsetQuit();
        /*
        Wake up all new entry nodes that were waiting on the node
         */
        synchronized (node){
            node.notifyAll();
        }

    }

    private void passOn(Token nextToken){
        NodeBean n;
        ManagedChannel channel;
        TokenServiceBlockingStub stub;
        GrpcServerResponse response;
        String t;
        t="";
        /*
        If the node hasn't found a valid next yet, wait till it has been found
         */
        synchronized (node){
            while(node.getNext().getId()==-1){
                try{
                    System.out.println("TokenManager wait for new next");
                    node.wait();
                }
                catch(InterruptedException e){
                    System.out.println("Thread was teared down while waiting for a valid next");
                }
            }
        }

        n=node.getNext();
        t=n.getIp().getHostAddress()+":"+n.getPort();

        System.out.println("Passing token to "+n.getId()+" which listen on "+t);

        try {
            try{Thread.sleep(3000);}catch(InterruptedException e){e.printStackTrace();}
            channel = ManagedChannelBuilder.forTarget(t).usePlaintext(true).build();
            stub = TokenServiceGrpc.newBlockingStub(channel);
            response = stub.tokenPass(nextToken);
            System.out.println("Next reply: "+response.getContents());
            channel.shutdown();
        }
        catch(StatusRuntimeException e) {
            System.out.println("TokenManager failed to pass the token cause next didn't reply");
        }


    }

    private void logToken(Token t){
        System.out.println("***************************");
        System.out.println("Token round: "+(t.getRound() ? 1 : 0));
        System.out.println("Number of participants: "+t.getParticipants());
        System.out.println("List of measurements: ");
        for(MeasurementProto e: t.getMeasurementsList())
            System.out.println("Id :"+e.getId()+" PM10 level: "+e.getValue());
    }


}
