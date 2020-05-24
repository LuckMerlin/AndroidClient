package com.merlin.task;

public abstract class AbsTask<T> implements Task<T> {
     private final Status mStatus=new Status(Status.INVALID,null);

     final void notifyUpdate(int status,String note,Object arg,Callback ...callbacks){
         mStatus.status(status).note(note).arg(arg);
    }

    public final boolean isStatus(int status,Status statusObj){
        Status curr=statusObj;
        return null!=curr&&status==curr.getStatus();
    }

    public final boolean isStatus(int status){
       return isStatus(status,mStatus);
    }

    @Override
    public final Status getStatus() {
        return mStatus;
    }
}
