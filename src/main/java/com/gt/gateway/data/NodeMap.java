package com.gt.gateway.data;

import java.util.HashMap;

import com.gt.beans.NodeBean;

/*
Singleton data structure to keep the list of nodes in the network. Synchronized resource
 */
public class NodeMap {

    private static NodeMap instance;
    /*
    A hash map has been used to have an efficient data structure for the operation : search_node_by_id.
    Less work to do in synchronized block to add a node
     */
    private final HashMap<Integer, NodeBean> nodeMap;

    /*
    Private constructor of the singleton
     */
    private NodeMap(){nodeMap=new HashMap<Integer, NodeBean>();}

    /*
    Static method to get the singleton instance. Synchronized to avoid double creation
     */
    public static synchronized NodeMap getInstance(){

        if(instance==null)
            instance=new NodeMap();
        return instance;
    }

    /*
    Method to add a node in the network, if not already a participant. Synchronized to avoid the inconsistent
    concurrent registrations
     */
    public synchronized HashMap<Integer,NodeBean> addNode(NodeBean n){
        if(nodeMap.containsKey(n.getId()))
            throw new InvalidIdException("Node with id:"+n.getId()+" is already in the network");
        System.out.println("Added node "+n);
        nodeMap.put(n.getId(),n);
        /*
        Return a copy of the nodeMap to avoid that concurrent update will change the object returned: this way
        the node will get a consistent list
         */
        return getNodeMap();
    }

    /*
    Return a consistent copy of the nodeMap up to a certain point
     */
    public synchronized HashMap<Integer, NodeBean> getNodeMap(){
        return new HashMap<Integer, NodeBean>(nodeMap);
    }

    /*
    Remove a node from the map
     */
    public synchronized void removeNode(int n){
        if(!nodeMap.containsKey(n))
            throw new InvalidIdException("Node with id:"+n+"  doesn't exist");
        System.out.println("Removed node "+n);
        nodeMap.remove(n);
    }

    /*
    Return the number of nodes in the map
     */
    public synchronized int getNumNodes(){
        return nodeMap.size();
    }


}
