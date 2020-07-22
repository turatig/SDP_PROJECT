package com.gt.node;

import com.gt.beans.NodeBean;
import com.gt.node.core.Node;
import com.gt.node.core.TokenManager;
import com.gt.node.server.RingServiceImpl;
import com.gt.node.server.TokenServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import com.gt.node.simulator.PM10Buffer;
import com.gt.node.simulator.PM10Simulator;
import com.gt.node.simulator.OutputThread;

import javax.ws.rs.ProcessingException;
import java.util.Random;

public class Main {
    public static void main(String[] args){

        NodeBean m;
        int id,port;
        String ip;
        Node node=new Node("http://localhost:4150/gateway/");
        int sleepTime;
        PM10Buffer buf=null;
        PM10Simulator simulator=null;
        OutputThread t=null;
        Server server=null;
        TokenManager tm=null;
        Random r = new Random();


        try {

            if(args.length==0) {
                /*
                Bound argument guarantees that id>0 and port>0
                */
                id=r.nextInt(Integer.MAX_VALUE);
                port=r.nextInt(65535);
                sleepTime=r.nextInt(10000);
            }
            else{
                id=Integer.parseInt(args[0]);
                port=Integer.parseInt(args[1]);
                sleepTime=args.length>2 ? Integer.parseInt(args[2]) : r.nextInt(10000);;
                /*
                Check if the port number given is valid
                 */
                assert port>0 && port<65535;
            }
            ip="127.0.0.1";

            /*
            Initializing buffering system and simulator thread. Buffer of size=256 and windowSize=12
            */
            buf=new PM10Buffer(256,12);
            simulator=new PM10Simulator(buf);
            t=new OutputThread(buf);
            simulator.start();
            t.start();


            server = ServerBuilder.forPort(port).addService(new RingServiceImpl(node)).
                    addService(new TokenServiceImpl(node)).build();

            server.start();
            System.out.println("Grpc server started");


            tm=new TokenManager(node);
            tm.start();
            System.out.println("TokenManager started");

            /*
            Register the node to the network
             */
            node.register(id,ip,port);
            for(NodeBean n: node.getNodesList()){
                System.out.println(n);
            }

            System.out.println("Sleeping for "+sleepTime+" ms");
            Thread.sleep(sleepTime);

            /*
            Call bootstrap protocol to link the node in the ring network
             */
            node.enter();

            System.out.println("*******************");
            System.out.println("PRESS ENTER TO STOP");
            System.out.println("*******************");

            System.in.read();

            System.out.println("*******************");
            System.out.println("THE NODE WILL STOP");
            System.out.println("*******************");

            /*
            Call exit protocol to unlink the node in the ring network
             */
            tm.setQuit();
            System.out.println("Sleeping for "+sleepTime+" ms before stopping");
            Thread.sleep(sleepTime);
            try{
                tm.join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            server.shutdown();
            simulator.stopMeGently();
            t.stop();

        }
        catch(ProcessingException e){
            System.out.println("The gateway failed to reply");
            tm.stop();
            server.shutdown();
            simulator.stopMeGently();
            t.stop();
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println("Shutting down node");
        }

        System.out.println("Quit");
    }

}
