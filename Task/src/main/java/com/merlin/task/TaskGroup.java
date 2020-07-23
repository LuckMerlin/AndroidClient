package com.merlin.task;

import com.task.debug.Debug;

import java.util.ArrayList;
import java.util.List;

public class TaskGroup extends Task{
    private final List<Task> mTasks=new ArrayList<>(1);
    private Task mExecuting=null;

    public TaskGroup(List<Task> tasks){
        add(tasks);
    }

    public final boolean add(List<Task> tasks){
        List<Task> global=null!=tasks&&tasks.size()>0?mTasks:null;
        return null!=global&&global.addAll(tasks);
    }

    public final boolean add(Task task){
        List<Task> global=null!=task?mTasks:null;
        return null!=global&&global.add(task);
    }

    public final boolean remove(Object task,boolean cancel,String debug){
        List<Task> global=null!=task?mTasks:null;
        int index=null!=global?global.indexOf(task):null;
        Task taskObj=index>=0&&index<global.size()?global.get(index):null;
        if (null!=taskObj&&global.remove(taskObj)){
            Debug.D("Remove task from group."+task);
            if (cancel){
                taskObj.cancel(true,"While remove from group.");
            }
            return true;
        }
        return false;
    }

    @Override
    protected final Canceler onExecute(Networker networker) {
        final List<Task> tasks=mTasks;
        if (null!=tasks&&tasks.size()>0){
            Task task=getNextUnFinishTask(mExecuting);
            if (null==task){
                Debug.D("All task of group executed."+this);
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"All task of group executed");
                return null;
            }
            final Canceler canceler=(mExecuting=task).execute(networker,
                    (int status, int what, String note, Object obj, Task child) ->{
                super.notifyStatus(status, what, note, obj);
            });
            mExecuting=null;
            return onExecute(networker);
        }
        return null;
    }

    public final int getFinishedSize(){
       List<Task> tasks= getFinished();
       return null!=tasks?tasks.size():-1;
    }

    public final int getSize(){
        List<Task> tasks=mTasks;
        return null!=tasks?tasks.size():-1;
    }

    public final Task getNextUnFinishTask(Task task){
        List<Task> tasks=null!=task?mTasks:null;
        if (null!=tasks){
            synchronized (tasks){
                int currIndex=tasks.indexOf(task);
                int size=tasks.size();
                if (currIndex>=0&&currIndex<size){
                    for (int i = currIndex; i < size; i++) {
                        if (null!=(task=tasks.get(i))&&task.isIdle()){
                            return task;
                        }
                    }
                }
            }
        }
        return null;
    }

    public final List<Task> getFinishSucceed(){
        return getTasks((Task task)->null!=task&&task.isFinishSucceed());
    }

    public final List<Task> getFinished(){
        return getTasks((Task task)->null!=task&&task.isFinished());
    }

    public final List<Task> getTasks(Matcher matcher) {
        List<Task> tasks=mTasks;
        int size=null!=tasks?tasks.size():-1;
        if (size>0){
            List<Task> result=null;
            if (null==matcher){
                (result=new ArrayList<>(size)).addAll(tasks);
            }else{
                result=new ArrayList(size);
                for (Task child:tasks) {
                    Boolean match=matcher.match(child);
                    if (null==match){
                        return result;
                    }else if (match){
                        result.add(child);
                    }
                }
            }
            return result;
        }
        return null;
    }

    public final Task getExecuting() {
        return mExecuting;
    }

    public final List<Task> getTasks() {
        return getTasks(null);
    }
}
