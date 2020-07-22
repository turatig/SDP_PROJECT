package com.gt.node.server;

import com.gt.beans.NodeBean;
import com.gt.grpc.RingServiceOuterClass.*;
import com.gt.grpc.RingServiceGrpc.RingServiceImplBase;
import com.gt.node.core.Node;
import io.grpc.stub.StreamObserver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RingServiceImpl extends RingServiceImplBase{

    private Node node;

    public RingServiceImpl(Node n){
        node=n;
    }

    @Override
    public void enterNext(Update up, StreamObserver<RingServiceResponse> observer){
        NodeProto n=up.getSender();
        int status=0;
        NodeProto r;
        RingServiceResponse response;
        try {
            NodeBean m = new NodeBean(n.getId(), InetAddress.getByName(n.getIp()), n.getPort());
             /*
            If the node is a valid new prev, reply status 0 : ok
             */
            if(node.enterNext(m))
                status=0;

            /*
            Else if the node isn't zombie, reply status 1 : redirect
             */
            else if (!node.isZombie()) {
                m = node.getNext();
                status = 1;
            }
            /*
            Else, reply status 2 : zombie
             */
            else
                status = 2;
            r=NodeProto.newBuilder().setId(m.getId()).setIp(m.getIp().getHostName())
                        .setPort(m.getPort()).build();
            response = RingServiceResponse.newBuilder().setStatus(status).
                        setNeighbor(r).build();
        }
        catch(UnknownHostException e){
            response=RingServiceResponse.newBuilder().setStatus(3).build();
        }
        observer.onNext(response);
        observer.onCompleted();
    }

    @Override
    public void enterPrev(Update up, StreamObserver<RingServiceResponse> observer){
        NodeProto n=up.getSender();
        NodeProto r;
        int status=0;
        RingServiceResponse response;
        try {
            NodeBean m = new NodeBean(n.getId(), InetAddress.getByName(n.getIp()), n.getPort());
            /*
            If the node is a valid new prev, reply status 0 : ok
             */
            if(node.enterPrev(m))
                status=0;

            /*
            Else if the node isn't zombie, reply status 1 : redirect
             */
            else if (!node.isZombie()) {
                m = node.getPrev();
                status = 1;
            }
            /*
            Else, reply status 2 : zombie
             */
            else
                status = 2;
            r=NodeProto.newBuilder().setId(m.getId()).setIp(m.getIp().getHostName())
                    .setPort(m.getPort()).build();
            response = RingServiceResponse.newBuilder().setStatus(status).
                        setNeighbor(r).build();

            observer.onNext(response);
            observer.onCompleted();

        }
        catch(UnknownHostException e){
            e.printStackTrace();
        }
    }


    @Override
    public void quitNext(Update up, StreamObserver<RingServiceResponse> observer){
        NodeProto sender=up.getSender();
        NodeProto r;
        NodeProto quittingNext=up.getUpdate();

        RingServiceResponse response;
        try {
            //Thread.sleep(node.getId()*400);
            NodeBean m = new NodeBean(sender.getId(), InetAddress.getByName(sender.getIp()), sender.getPort());
            NodeBean n=  new NodeBean(quittingNext.getId(), InetAddress.getByName(quittingNext.getIp()),
                    quittingNext.getPort());
            /*
            If the sender node is a valid next reply  status 0 : ok
             */
            if(node.quitNext(m,n))
                response=RingServiceResponse.newBuilder().setStatus(0).build();
            /*
            Else reply reply status 1 : redirect
             */
            else {
                m=node.getNext();
                r=NodeProto.newBuilder().setId(m.getId()).setIp(m.getIp().getHostName())
                        .setPort(m.getPort()).build();
                response = RingServiceResponse.newBuilder().setStatus(1).
                        setNeighbor(r).build();
            }


        }
        catch(UnknownHostException e){
            response=RingServiceResponse.newBuilder().setStatus(2).build();
        }
        /*catch(InterruptedException e){
            response=null;
        }*/
        observer.onNext(response);
        observer.onCompleted();
    }

}
