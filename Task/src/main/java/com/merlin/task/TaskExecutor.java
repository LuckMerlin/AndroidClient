package com.merlin.task;

import android.os.Handler;
import android.os.Looper;

import com.task.debug.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class TaskExecutor {
    private final List<Task> mTasks=new ArrayList<>();
    private final WeakHashMap<OnTaskUpdate,Matcher> mListener=new WeakHashMap<>(1);
    private final Handler mHandler;
    private Networker mNetworker;
    private Executor mExecutor;

    public TaskExecutor(Handler handler){
        mHandler=null!=handler?handler:new Handler(Looper.getMainLooper());
    }

    public boolean addTask(Task task,String debug){
        List<Task> tasks=null!=task?mTasks:null;
        if (null!=tasks&&!tasks.contains(task)&&tasks.add(task)){
            Debug.D("Added task "+task+" "+(null!=debug?debug:"."));
            notifyStatus(Status.ADD,What.WHAT_NONE,"Add task",null,task,null);
            return true;
        }
        return false;
    }

    public int removeTask(Matcher matcher,Integer action,String debug){
        List<Task> tasks=null!=matcher?getTasks(matcher,-1):null;
        int size=null!=tasks?tasks.size():-1;
        List<Task> list=mTasks;
        if (size>0&&null!=list){
            for (Task child:tasks) {
                if (null==child){
                    continue;
                }
                if (null!=action) {
                    switch (action) {
                        case Status.PAUSE:
                            child.pause(true, "Before remove task " + (null != debug ? debug : "."));
                            break;
                    }
                }
                list.remove(child);
                Debug.D("Removed task "+child+" "+(null!=debug?debug:"."));
                notifyStatus(Status.REMOVE,What.WHAT_NONE,"Remove task",null,child,null);
            }
            return size;
        }
        return -1;
    }

    public boolean put(OnTaskUpdate update, Matcher matcher){
        WeakHashMap<OnTaskUpdate,Matcher> listener=null!=update?mListener:null;
        if (null!=listener){
            synchronized (listener){
                listener.put(update,matcher);
                return true;
            }
        }
        return false;
    }

    public boolean remove(OnTaskUpdate update){
        WeakHashMap<OnTaskUpdate,Matcher> listener=null!=update?mListener:null;
        if (null!=listener){
            synchronized (listener){
                if (listener.containsKey(update)) {
                    listener.remove(update);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean start(String debug){
        Task task=findNextUnFinish();
        return null!=task&&start(task,debug,null);
    }

    public final boolean start(final Matcher matcher, String debug,OnTaskUpdate update){
        List<Task> tasks=getTasks(matcher,-1);
        if (null!=tasks&&tasks.size()>0){
            for (Task child:tasks){
                if (null!=child&&!child.isDoing()&&!child.isFinishSucceed()){
                    start(child,debug,update);
                }
            }
        }
        return false;
    }

    public boolean start(final Task task, String debug,OnTaskUpdate update){
        if (null==task){
            Debug.W("Can't execute task which is NULL "+(null!=debug?debug:"."));
            return false;
        }
        if (task.isFinishSucceed()&&(!(task instanceof OnResolveStart)||!((OnResolveStart)task).onResolveStart())){
            Debug.D("Not need execute task which already execute succeed.");
            return false;//Already execute succeed
        }
        final Runnable runnable=()->{
            Debug.D("Start task "+task);
            notifyStatusOnUiThread(Status.START,What.WHAT_NONE,"Start task",null,task,update);
            task.execute(mNetworker,(int status, int what, String note, Object obj, Task t)->notifyStatusOnUiThread(status,what,note,obj,t,update));
            notifyStatusOnUiThread(Status.STOP,What.WHAT_NONE,"Stop task",null,task,update);
            Debug.D("Stop task "+task);
        };
        final Executor executor=mExecutor;
        if (null!=executor){
            executor.execute(runnable);
        }else{
            new Thread(runnable).start();
        }
        return true;
    }

    public Task findNextUnFinish(){
        List<Task> tasks=getTasks((task)->null!=task&&task.getStatusCode()==Status.IDLE,1);
        return null!=tasks&&tasks.size()>0?tasks.get(0):null;
    }

    public boolean pause(Matcher matcher,String debug){
        List<Task> tasks=getTasks(matcher,-1);
        if (null!=tasks&&tasks.size()>0){
            for (Task child:tasks){
                if (null!=child&&child.isDoing()&&child instanceof PauseTask){
                    ((PauseTask)child).pause(true,debug);
                }
            }
            return true;
        }
        return false;
    }

    public boolean cancel(Matcher matcher,String debug){
        List<Task> tasks=getTasks(matcher,-1);
        if (null!=tasks&&tasks.size()>0){
            for (Task child:tasks){
                if (null!=child&&child.isDoing()&&child instanceof CancelTask){
                    ((CancelTask)child).cancel(true,debug);
                }
            }
            return true;
        }
        return false;
    }

    public final List<Task> getTasks(Matcher matcher,int max){
        List<Task> tasks=mTasks;
        int length=null!=tasks?tasks.size():-1;
        if (length>0){
            List<Task> result=new ArrayList<>(length);
            synchronized (tasks){
                Boolean match=null;
                for (Task child:tasks){
                    if (null!=child){
                        match=null!=matcher?matcher.match(child):true;
                        if (null==match){
                            break;
                        }else if (!match){
                            continue;
                        }
                        result.add(child);
                        if (max>0&&(max<0||result.size()>=max)){
                            break;
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    private void notifyStatusOnUiThread(int status, int what, String note, Object obj, Task task,OnTaskUpdate update){
        if (Thread.currentThread()==Looper.getMainLooper().getThread()){
            notifyStatus(status,what,note,obj,task,update);
        }else{
            mHandler.post(()->notifyStatus(status,what,note,obj,task,update));
        }
    }

    private void notifyStatus(int status, int what, String note, Object obj, Task task,OnTaskUpdate update){
        WeakHashMap<OnTaskUpdate,Matcher> listeners=mListener;
        synchronized (listeners){
            Set<OnTaskUpdate> set=null!=listeners?listeners.keySet():null;
            if (null!=set&&set.size()>0){
                for (OnTaskUpdate child:set){
                    Matcher matcher=listeners.get(child);
                    if (null==matcher||matcher.match(task)){
                        child.onTaskUpdate(status,what,note,obj,task);
                    }
                }
            }
        }
        if (null!=update){
            update.onTaskUpdate(status,what,note,obj,task);
        }
    }
}
