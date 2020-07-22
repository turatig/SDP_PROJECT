package com.gt.node.simulator;

import com.gt.beans.Measurement;

/****
    Buffer keep tracks of all consumed measurements. The simulator thread produces them and another one consumes
    them. Value already consumed will be overwritten by new measurements
*/
public class PM10Buffer implements Buffer {

    private Measurement [] buf;
    /*
    Total size of the buffer
     */
    private int size;
    /*
    Index of the first item to be consumed
     */
    private int startIdx;
    /*
    Total number of items to be consumed
     */
    private int toBeConsumed;
    /*
    Size of the sliding window
     */
    private int windowSize;

    public PM10Buffer(int s,int ws){
        size=s;
        startIdx=0;
        toBeConsumed=0;
        windowSize=ws;
        buf=new Measurement[size];
    }

    @Override
    public synchronized void addMeasurement(Measurement m){
        /*
        If the buffer is full, then wait
         */
        while(toBeConsumed>=size){
            try{
                wait();
            }
            catch (InterruptedException e){
                System.out.println("The thread was teared down");
            }
        }
        /*try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e){
            System.out.println(e.getMessage());
        }*/
        buf[(startIdx+toBeConsumed)%size]=m;
        //System.out.println("Inserting "+m);
        toBeConsumed++;

        /*
        If the buffer is no more empty, notify to consumer
         */
        if(toBeConsumed>=windowSize)
            notifyAll();
    }

    /*
    Method to consume items
     */
    public synchronized Measurement[] consumeWindow(){
        Measurement[] m=new Measurement[windowSize];
        double mean=0;
        /*
        Checking if there are enough items to be consumed is a synchronized operation.
         */
        while(toBeConsumed<windowSize){
            //System.out.println("Not enough value");
            try{
                wait();
            }
            catch (InterruptedException e){
                System.out.println("The thread was teared down");
            }
        }
        //System.out.println("Consumer woke up");
        for(int i=0;i<windowSize;i++)
            m[i]=buf[(startIdx+i)%size];

        /*
        Updating values of toBeConsumed is a synchronized operation. Notify to the producer that the buffer
        is no more full.
         */
        /*
        50% overlap
         */
        toBeConsumed-=windowSize/2;
        notifyAll();
        /*
        Shift the window forward
         */
        startIdx=(startIdx+windowSize/2)%size;
        return m;
    }
}
