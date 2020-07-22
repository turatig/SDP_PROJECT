package com.gt.gateway.data;

import com.gt.beans.Measurement;

import java.util.ArrayList;
import java.util.Collections;

/*
Singleton data structure to keep the data collected by nodes
 */
public class Records {

    /*
    Singleton instance
     */
    private static Records instance;
    /*
    Considered the APPEND_ONLY nature of this data structure an ArrayList has been chosen to implement it.
     */
    private ArrayList<Measurement> data;
    /*
    Private constructor
     */
    private Records(){data=new ArrayList<Measurement>();}

    /*********
     *The concurrency problem envolves sensor that writes data (producer) and analyst that read them (consumer)
     */
    /*
    Method to access singleton object must be synchronized to avoid multiple instances
     */
    public static synchronized Records getInstance(){
        if(instance==null)
            instance=new Records();
        return instance;
    }
    /*
    Must be synchronized cause the list can be accessed by more than one thread
     */
    public synchronized void addMeasurement(Measurement m){
        data.add(m);
        /*
        Keep the list ordered by timestamp if data arrives out of order
         */
        Collections.sort(data,Collections.reverseOrder());
        System.out.println("Added measurement "+ m);
    }

    /*
    Method to return a copy of the list must be synchronized to guarantee consistency
     */
    public synchronized ArrayList<Measurement> getRecords(){
        return new ArrayList<Measurement>(data);
    }

}
