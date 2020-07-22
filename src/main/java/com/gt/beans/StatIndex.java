package com.gt.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
/*
Bean to wrap a statistical index returned by gateway
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StatIndex {

    /*
    This is used to save the type of the statistical index (mean,variance,count...)
     */
    private String type;
    private double value;

    public StatIndex(){
        value=0;
        type=null;
    }
    public StatIndex(double n, String t){
        setValue(n);
        setType(t);
    }

    public void setValue(double n){value=n;}
    public void setType(String t){type=t;}
    public double getValue(){return value;}
    public String getType(){return type;}

    public String toString(){
        return getType()+" = "+getValue();
    }

}
