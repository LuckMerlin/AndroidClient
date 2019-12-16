package com.merlin.transport;

public class Transport {
    private final String mName;
    private final String mFrom,mTo;
    private final long mStartTime;


    public Transport(String name,String from,String to,long startTime){
        mStartTime=startTime;
        mFrom=from;
        mTo=to;
        mName=name;
    }

    public String getName() {
        return mName;
    }


}
