package com.gt.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/*
An adapter class to serialized InetAddress
 */
public class InetAddressAdapter extends XmlAdapter<String,InetAddress>{

    public InetAddress unmarshal(String ip) throws UnknownHostException {
        return InetAddress.getByName(ip);
    }

    public String marshal(InetAddress ip){
        return ip.getHostAddress();
    }
}
