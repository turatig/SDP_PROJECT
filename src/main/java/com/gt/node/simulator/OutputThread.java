package com.gt.node.simulator;

import com.gt.beans.Measurement;

/*
Threads of this class are compute aggregate values from buffer of some specific type sensors
 */
public class OutputThread extends Thread {

    /*
    Shared with the simulator
     */
    private PM10Buffer in;
    private volatile boolean stopCondition;
    private String type;

    public OutputThread(PM10Buffer buf){
        in=buf;
        stopCondition=false;
        type="PM10";
    }

    public void run(){
        Measurement [] m;
        while(!stopCondition)
            OutputQueue.getInstance().addMeasurement(getMean(in.consumeWindow()));
    }

    /*
    Utility function to compute the mean
     */
    private Measurement getMean(Measurement[] m){
        double mean=0;
        for(int i=0;i<m.length;i++)
            mean+=m[i].getValue();
        mean/=m.length;
        /*
        The id=MEAN because it's an aggregate value
         */
        return new Measurement("MEAN",type,
                        mean,System.currentTimeMillis());
    }

    public void stopMeGently(){stopCondition=true;}
}
