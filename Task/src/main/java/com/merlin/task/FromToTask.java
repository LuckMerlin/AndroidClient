package com.merlin.task;

public abstract class FromToTask<T,V> extends Task {
    private final T mFrom;
    private final V mTo;

    public FromToTask(T from,V to){
        mFrom=from;
        mTo=to;
    }

    public final T getFrom() {
        return mFrom;
    }

    public final V getTo() {
        return mTo;
    }
}
