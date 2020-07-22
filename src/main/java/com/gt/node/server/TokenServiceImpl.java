package com.gt.node.server;

import com.gt.beans.NodeBean;
import com.gt.grpc.GrpcServerResponseOuterClass.GrpcServerResponse;
import com.gt.grpc.TokenServiceGrpc;
import com.gt.grpc.TokenServiceOuterClass.Token;
import io.grpc.stub.StreamObserver;

import com.gt.node.core.Node;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TokenServiceImpl extends TokenServiceGrpc.TokenServiceImplBase {

    /*
    This service delivers new token to the node
     */
    private Node node;
    public TokenServiceImpl(Node n){
        node=n;
    }
    @Override
    public void tokenPass(Token t, StreamObserver<GrpcServerResponse> observer){
        /*
        If it's an exit token, call removePrev
         */
        if(t.getExit()) {
            try {
                NodeBean sender = new NodeBean(t.getUpdate().getSender().getId(),
                        InetAddress.getByName(t.getUpdate().getSender().getIp()),
                        t.getUpdate().getSender().getPort());
                NodeBean update = new NodeBean(t.getUpdate().getUpdate().getId(),
                        InetAddress.getByName(t.getUpdate().getUpdate().getIp()),
                        t.getUpdate().getUpdate().getPort());

                node.quitPrev(sender,update);
            }
            catch(UnknownHostException e){
                e.printStackTrace();
            }
        }
        node.setNewToken(t);
        GrpcServerResponse response=GrpcServerResponse.newBuilder().
                setStatus(0).setContents("Ok").build();
        observer.onNext(response);
        observer.onCompleted();
    }
}
