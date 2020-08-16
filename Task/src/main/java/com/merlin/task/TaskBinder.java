package com.merlin.task;

import android.os.Binder;

import com.task.debug.Debug;

import java.util.List;

public class TaskBinder extends Binder {
    private final TaskExecutor mExecutor;

    public
    TaskBinder(TaskExecutor executor){
        mExecutor=executor;
    }

    public final boolean put(OnTaskUpdate update,Matcher matcher){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.put(update,matcher);
    }

    public final boolean remove(OnTaskUpdate update){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.remove(update);
    }

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

    public final boolean start(Matcher matcher,String debug,OnTaskUpdate update){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.start(matcher,debug,update);
    }

    public final boolean start(String debug){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.start(debug);
    }

    public final boolean pause(Matcher matcher,String debug){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.pause(matcher,debug);
    }

    public final boolean cancel(Matcher matcher,String debug){
        TaskExecutor executor=mExecutor;
        return null!=executor&&executor.cancel(matcher,debug);
    }

}
