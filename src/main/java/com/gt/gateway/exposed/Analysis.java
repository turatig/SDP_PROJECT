package com.gt.gateway.exposed;

import com.gt.beans.Measurement;
import com.gt.beans.StatIndex;
import com.gt.gateway.data.NodeMap;
import com.gt.gateway.data.Records;

import java.util.List;
import java.util.ArrayList;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;

/***
 * Resource exposed at "gateway/analysis
 */
@Path("analysis")
public class Analysis {

    /***********
     *
     * REST interface method exposed at /analysis/nodes/count to get the number of nodes in p2p network
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("nodes/count")
    public StatIndex getNumber(){

        return new StatIndex((double) NodeMap.getInstance().getNumNodes(),"nodes_count");
    }

    /***********
     *
     * REST interface method exposed at /analysis/records/{lastn} to get the last n records
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("records/{lastn}")
    public List<Measurement> getLastRecords(@PathParam("lastn") int n){
        /*
        If n<0 empty list will be returned
         */
        ArrayList<Measurement> records=Records.getInstance().getRecords();
        return getLast(records,n);

    }

    /***********
     *
     * REST interface method exposed at /analysis/records/{lastn} to get the mean of the last n records
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("records/mean/{lastn}")
    public StatIndex getMean(@PathParam("lastn") int n){
        /*
        If n<0 mean=0.0 will be returned
         */
        ArrayList<Measurement> records=Records.getInstance().getRecords();
        return meanCompute(getLast(records,n));
    }

    /***********
     *
     * REST interface method exposed at /analysis/records/{lastn} to get the variance of the last n records
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("records/variance/{lastn}")
    public StatIndex getVariance(@PathParam("lastn") int n){
        ArrayList<Measurement> records=Records.getInstance().getRecords();
        return varianceCompute(getLast(records,n));
    }

    /*
    Utility function to get the last n elements in temporal descending order
     */
    private static ArrayList<Measurement> getLast(ArrayList<Measurement> records,int n){
        ArrayList<Measurement> lastn=new ArrayList<Measurement>();

        /*
        Getting last n records
         */
        for(int i=0;i<n && i<records.size();i++)
            lastn.add(records.get(i));

        return lastn;

    }
    /*
    Utility function to compute the mean of a list of measurements
     */
    private static StatIndex meanCompute(List<Measurement> records){
        double mean=0;

        for(int i=0;i<records.size();i++)
            mean+=records.get(i).getValue();

        mean=records.size()!=0 ? mean/records.size() : mean;
        return new StatIndex(mean,"mean");

    }
    /*
    Utility function to compute the variance
     */
    private static StatIndex varianceCompute(List<Measurement> records){
        double var=0;
        double mean=meanCompute(records).getValue();


        for(int i=0;i<records.size();i++)
            var+=Math.pow(mean-records.get(i).getValue(),2);

        var=records.size()>1 ? var/(records.size()-1) : var;
        return new StatIndex(var,"variance");

    }



}
