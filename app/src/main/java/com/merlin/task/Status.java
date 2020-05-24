package com.merlin.task;

public class Status implements State{
    private int mStatus;
    private String mNote;
    private Object mArg;

    public Status(int status,Object arg){
        mStatus=status;
        mArg=arg;
    }

    final Status status(int status){
        mStatus=status;
        return this;
    }

    final Status note(String note){
        mNote=note;
        return this;
    }

    final Status arg(Object arg){
        mArg=arg;
        return this;
    }

    public final int getStatus() {
        return mStatus;
    }

    public final Object getArg() {
        return mArg;
    }
}
