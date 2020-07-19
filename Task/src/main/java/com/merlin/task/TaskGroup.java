package com.merlin.task;

import java.util.ArrayList;
import java.util.List;

public class TaskGroup extends Task{
    private final List<Task> mTasks=new ArrayList<>(1);

    public TaskGroup(List<Task> tasks){
        add(tasks);
    }

    public boolean add(List<Task> tasks){
        if (null!=tasks&&tasks.size()>0){
            mTasks.addAll(tasks);
            return true;
        }
        return false;
    }


    @Override
    protected Canceler onExecute(Networker networker) {
        return null;
    }
}
