package com.merlin.transport;

import androidx.annotation.NonNull;

public class ConveyStatus implements Status {
    private int mStatus=IDLE;
    private Object mObject;
    protected ConveyStatus(){
        this(IDLE,null);
    }
    protected ConveyStatus(int status,Object object){
        mStatus=status;
        mObject=object;
    }

    protected final ConveyStatus updateStatus(int status,Object obj){
        mStatus=status;
        mObject=obj;
        return this;
    }
    public final int getStatus() {
        return mStatus;
    }

    public Object getStatusObject(int status){
        int currStatus=mStatus;
        return currStatus==status?mObject:null;
    }

    public final boolean isStatus(int ...status){
        if (null!=status&&status.length>0){
            int current=mStatus;
            for (int child:status) {
                if (current==child){
                    return true;
                }
            }
        }
        return false;
    }

    public final Object getObject() {
        return mObject;
    }

    @NonNull
    @Override
    public String toString() {
        return ""+mStatus+" "+mObject+" "+super.toString();
    }
}
