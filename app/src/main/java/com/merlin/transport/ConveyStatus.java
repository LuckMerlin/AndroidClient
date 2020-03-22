package com.merlin.transport;

public class ConveyStatus {
    public final static int IDLE=12312;
    public final static int ADD=12313;
    public final static int PREPARING=12314;
    public final static int PREPARED=12315;
    public final static int STARTED=12317;
    public final static int PAUSED=12318;
    public final static int CANCELED=12319;
    public final static int FINISHED=12320;
    public final static int PROGRESS=12321;
    public final static int CREATE=12322;
    public final static int DESTROY=12323;
    private int mStatus=IDLE;
    private Object mObject;

    protected ConveyStatus(int status,Object object){
        mStatus=status;
        mObject=object;
    }

    protected final ConveyStatus update(int status,Object obj){
        mStatus=status;
        mObject=obj;
        return this;
    }
    public final int getStatus() {
        return mStatus;
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
}
