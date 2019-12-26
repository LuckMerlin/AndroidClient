package com.merlin.breakpoint;

import com.merlin.task.Download;

public class BreakPoint {
    private final Download mTask;

    public BreakPoint(){
        this(null);
    }

    public BreakPoint(Download task){
        mTask=task;
    }

    public Download getTask() {
        return mTask;
    }
}
