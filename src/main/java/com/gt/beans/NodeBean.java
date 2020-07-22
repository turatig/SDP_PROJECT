package com.gt.beans;

import java.net.InetAddress;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/*
Class for external representation of a node
 */
@XmlRootElement(name="node")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeBean implements Comparable<NodeBean>{

    /*ID of the node in the network
     */
    private int id;

    @XmlJavaTypeAdapter(InetAddressAdapter.class)
    private InetAddress ip;
    /*Port used to communicate with other nodes
     */
    private int port;

    public NodeBean(){}

    public NodeBean(int n, InetAddress i, int p){
        setId(n);
        setIp(i);
        setPort(p);
    }

    public NodeBean(NodeBean n){
        setId(n.getId());
        setIp(n.getIp());
        setPort(n.getPort());
    }
    /*Getters
    */
    public int getId(){return id;}
    public InetAddress getIp(){return ip;}
    public int getPort(){return port;}
    /*Setters
     */
    public void setId(int n){ id=n; }
    public void setIp(InetAddress i){ ip=i; }
    public void setPort(int p){ port=p; }

    public String toString(){ return "Id : "+getId()+
            " Ip : "+getIp()+
            " Port : "+getPort();
    }

    /*
    Two nodes are compared by their id
     */
    @Override
    public int compareTo(NodeBean n){ return this.getId()-n.getId(); }

    public boolean equals(NodeBean n){
        return getId()==n.getId() && getIp().equals(n.getIp()) && getPort()==n.getPort();
    }

}
