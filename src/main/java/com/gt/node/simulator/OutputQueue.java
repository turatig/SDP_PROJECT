package com.gt.node.simulator;

import com.gt.beans.Measurement;

import java.util.LinkedList;


/*
Singleton: output buffer of the node. Automaton can just peek output values from here. Output thread puts new
means computed here.
 */
public class OutputQueue implements Buffer{

    /*
    This buffer is implemented as a queue
     */
    private LinkedList<Measurement> buf;
    private static OutputQueue instance;

    private OutputQueue(){
        buf=new LinkedList<Measurement>();
    }
    /*
    Automaton and OutputThread concurrently update this OutputQueue
     */
    public synchronized static OutputQueue getInstance(){
        if(instance==null)
            instance=new OutputQueue();
        return instance;
    }

    @Override
    public synchronized void addMeasurement(Measurement m){ buf.add(m); }

    public synchronized Measurement consumeMeasurement(){
        /*
        Retrieve and remove. Return null if buf is empty
         */
       return buf.poll();
    }
}
