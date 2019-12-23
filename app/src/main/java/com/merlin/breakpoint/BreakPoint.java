package com.merlin.breakpoint;

import com.merlin.task.DownloadTask;

public class BreakPoint {
    private final DownloadTask mTask;

    public BreakPoint(){
        this(null);
    }

    public BreakPoint(DownloadTask task){
        mTask=task;
    }

    public DownloadTask getTask() {
        return mTask;
    }
}
