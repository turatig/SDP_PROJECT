package com.gt.analyst;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;


import com.gt.beans.StatIndex;
import com.gt.beans.Measurement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.List;


/****
 * Class for the analyst client
 */
public class Analyst {

    public static final String GATEWAY_URI="http://localhost:4150/gateway/";

    public static void main(String[] args){
        /*
        Client set up
         */
        Client c=ClientBuilder.newClient();
        WebTarget target=c.target(GATEWAY_URI);

        /*
        Object that wraps a statistical index returned by gateway
         */
        double statIdx;
        List<Measurement> lastStats;
        int nodesCount=0,param=0;
        BufferedReader userIn=new BufferedReader(new InputStreamReader(System.in));
        int selector=0;

        do{
            selector=menu(userIn);

            try{
                switch(selector){
                    case 1:
                        nodesCount=(int)target.path("analysis/nodes/count").request(MediaType.APPLICATION_XML).
                                get(new GenericType<StatIndex>(){}).getValue();
                        System.out.println("********************");
                        System.out.println("\n\nThere are "+nodesCount+" nodes in the network\n\n");
                        System.out.println("********************");
                        break;
                    case 2:
                        try{
                            param=acquireParams(userIn);
                            lastStats=target.path("analysis/records/"+param).request(MediaType.APPLICATION_XML).
                                    get(new GenericType<List<Measurement>>(){});
                            System.out.println("********************");
                            for(Measurement e: lastStats){
                                System.out.println(e);
                            }
                            System.out.println("********************");
                        }
                        /*
                        If IOException occurs close the client
                         */
                        catch (IOException e){
                            selector=0;
                        }
                        break;
                    case 3:
                        try{
                            param=acquireParams(userIn);
                            statIdx=target.path("analysis/records/mean/"+param).request(MediaType.APPLICATION_XML).
                                    get(new GenericType<StatIndex>(){}).getValue();
                            System.out.println("********************");
                            System.out.println("\n\nMean of the last "+param+" measurements is "+statIdx+"\n\n");
                            System.out.println("********************");
                        }
                        /*
                        If IOException occurs close the client
                         */
                        catch (IOException e){
                            selector=0;
                        }
                        break;
                    case 4:
                        try{
                            param=acquireParams(userIn);
                            statIdx=target.path("analysis/records/variance/"+param).request(MediaType.APPLICATION_XML).
                                    get(new GenericType<StatIndex>(){}).getValue();
                            System.out.println("********************");
                            System.out.println("\n\nVariance of the last "+param+" measurements is "+statIdx+"\n\n");
                            System.out.println("********************");
                        }
                        /*
                        If IOException occurs close the client
                         */
                        catch (IOException e){
                            selector=0;
                        }
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }while(selector!=0);
    }

    /*
    Display menu options and validate the input. Operation selector will be returned
     */
    public static int menu(BufferedReader userIn){
        boolean error;
        int selector=0;

        do{
            error=false;
            System.out.println("Select one of the following:");
            System.out.println("[0] : to close client");
            System.out.println("[1] : get the number of nodes in the network");
            System.out.println("[2] : get the last n measurements with timestamps");
            System.out.println("[3] : get the mean of the last n measurements");
            System.out.println("[4] : get the variance of the last n measurements");

            try{
                selector=Integer.parseInt(userIn.readLine());
                assert selector>=0 && selector<=4;
            }
            catch(IOException e){
                System.out.println("I/O error: client will be closed");
            }
            catch(Exception e){
                System.out.println("Invalid input: please choose a valid numeric option");
                error=true;
            }
        }while(error);

        return selector;
    }

    /*
    Acquire and validate integer parameter required for the query
     */
    public static int acquireParams(BufferedReader userIn)throws IOException{
        boolean error;
        int res=0;

        do{
            error=false;
            System.out.println("Insert an integer parameter:");
            try{
                res=Integer.parseInt(userIn.readLine());
                assert res>=0;
            }
            catch(IOException e){
                System.out.println("I/O error: client will be closed");
                throw e;
            }
            catch(Exception e){
                System.out.println("Invalid input: non-negative integer parameter is required for the query");
                error=true;
            }
        }while(error);

        return res;

    }

}
