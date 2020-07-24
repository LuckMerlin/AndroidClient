package com.merlin.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

public abstract class TaskService extends Service {
    private final TaskExecutor mExecutor=new TaskExecutor();

    public final List<Task> getTasks(Matcher matcher, int max){
        TaskExecutor executor=mExecutor;
        return null!=executor?executor.getTasks(matcher, max):null;
    }

    public final boolean addTask(Task task,String debug){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.addTask(task, debug);
    }

    public final int removeTask(Matcher matcher,int action,String debug){
        TaskExecutor executor=mExecutor;
        return null!=executor?executor.removeTask(matcher, action,debug):-1;
    }

}
