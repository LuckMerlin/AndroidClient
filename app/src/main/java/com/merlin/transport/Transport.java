package com.merlin.transport;

import com.merlin.task.Status;

public class Transport {
    private final String mName;
    private final String mMD5;
    private final String mFrom,mTo;
    private final long mStartTime;
    private final long mTotal;
    private final Status mStatus;


    public Transport(String name,String ms5,String from,String to,long startTime,long total,Status status){
        mStatus=status;
        mStartTime=startTime;
        mFrom=from;
        mMD5=ms5;
        mTo=to;
        mName=name;
        mTotal=total;
    }

    public String getName() {
        return mName;
    }

    public float getSpeed() {
        Status status=mStatus;
        return null!=status?status.getSpeed():-1;
    }

    public long getDoned() {
        Status status=mStatus;
        return null!=status?status.getDoned():-1;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getTotal() {
        return mTotal;
    }

    public String getFrom() {
        return mFrom;
    }

    public String getTo() {
        return mTo;
    }

    public String getMD5() {
        return mMD5;
    }
}
