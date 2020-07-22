package com.gt.gateway.exposed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericEntity;

import com.gt.beans.Measurement;
import com.gt.beans.NodeBean;
import com.gt.gateway.data.InvalidIdException;
import com.gt.gateway.data.NodeMap;
import com.gt.gateway.data.Records;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;



/**
 * Resource (exposed at "gateway/nodes" path)
 */
@Path("nodes")
public class Nodes {


    /***********
     *
     * REST interface method exposed at /nodes/login to register a node to the p2p network
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("login")
    public Response login(NodeBean n) {
        HashMap<Integer,NodeBean> nodeMap;
        /*
        Checking if the REST client is specifying all the required information and they have acceptable values
         */
        if(!validateNode(n))
            return Response.status(400).entity("Invalid node values").build();

        try {
            nodeMap=NodeMap.getInstance().addNode(n);
        }
        /*
        If there's another node in the network with the same id
         */
        catch(InvalidIdException e){
            return Response.status(400).entity(e.getMessage()).build();
        }
        /*
        Transforming HashMap into an ArrayList.
         */
        List<NodeBean> neighborhood=new ArrayList<NodeBean>();
        for(Map.Entry<Integer, NodeBean> e: nodeMap.entrySet()){
            neighborhood.add(e.getValue());
        }
        return Response.ok().entity(new GenericEntity<List<NodeBean>>(neighborhood){}).build();
    }

    /***********
     *
     * REST interface method exposed at /nodes/map to get the list of nodes in the p2p network. (Currently
     * not used by nodes).
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("list")
    public List<NodeBean> getNodeList(){

        //Converting HashMap<Integer,Node> to List<Node>
        HashMap<Integer, NodeBean> nodeMap= NodeMap.getInstance().getNodeMap();

        List<NodeBean> neighborhood=new ArrayList<NodeBean>();
        for(Map.Entry<Integer, NodeBean> e: nodeMap.entrySet()){
            neighborhood.add(e.getValue());
        }

        return neighborhood;
    }

    /***********
     *
     * REST interface method exposed at /nodes/login to remove a node from the p2p network
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id}")
    public Response remove(@PathParam("id") int n){
        try{
            NodeMap.getInstance().removeNode(n);
            return Response.ok().build();
        }
        catch(InvalidIdException e){
            return Response.status(404).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("add")
    public Response addMeasureament(Measurement m){
        /*
        Checking if the REST client is specifying all the required information and they have acceptable values
         */
        if(!validateMeasurement(m))
            return Response.status(400).entity("Invalid measurements values").build();
        Records.getInstance().addMeasurement(m);
        return Response.ok().build();
    }

    /***********
     *Checks on the validity of a measurement messages sent by clients
     */
    private static boolean validateMeasurement(Measurement m){
        /*
        A valid measurement must have an id
         */
        if(m.getId()==null)
            return false;
        /*
        A valid measurement must have non-negative timestamp and value
         */
        if(m.getTimestamp()<0)
            return false;
        if(m.getValue()<0)
            return false;
        /*
        For the scope of this project, we've only type="PM10" records
         */
        if(!m.getType().equals("PM10"))
            return false;


        return true;
    }



    /***********
     *Checks on the validity of node messages sent by clients
     */
    private static boolean validateNode(NodeBean n){

        if(n.getId()<0)
            return false;

        if(n.getIp()==null)
            return false;

        /*
        Checking if port>0 to exclude wildcard port number
         */
        if(n.getPort()<=0 || n.getPort()>65535)
            return false;

        return true;
    }

}
