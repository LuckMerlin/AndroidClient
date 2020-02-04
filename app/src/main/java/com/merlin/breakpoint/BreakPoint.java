package com.merlin.breakpoint;

import com.merlin.task.Transport;

public class BreakPoint {
    private final Transport mTask;

    public BreakPoint(){
        this(null);
    }

    public BreakPoint(Transport task){
        mTask=task;
    }

    public Transport getTask() {
        return mTask;
    }
}
