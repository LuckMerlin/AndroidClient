package com.merlin.task;

import android.os.Looper;

import com.task.debug.Debug;

import java.util.WeakHashMap;

public abstract class Task implements Status{
  private final String mName;
  private final TaskStatus mStatus=new TaskStatus();
  private WeakHashMap<OnTaskUpdate,Long> mReference;
  private boolean mPaused=false;
  private Result mResult;
  private Progress mProgress;
  private OnTaskUpdate mOnTaskUpdate;

  public Task(String name){
      mName=name;
  }

  public final TaskStatus getStatus(){
        return mStatus;
  }

  public final Progress getProgress() {
        return mProgress;
  }

  public final Result getResult() {
      return mResult;
  }

  protected abstract void onExecute(Networker networker);

  public final  boolean isIdle(){
      return getStatusCode()==Status.IDLE;
  }

  public final boolean isDoing(){
      int code=getStatusCode();
      return code!=Status.FINISH&code!=Status.IDLE;
  }

  public final void execute(Networker networker,OnTaskUpdate update){
      Looper mainLooper=Looper.getMainLooper();
      Thread mainThread=null!=mainLooper?mainLooper.getThread():null;
      Thread currentThread=Thread.currentThread();
      if (null!=mainThread&&(null!=currentThread&&currentThread==mainThread)){
          throw new IllegalThreadStateException("Task can't execute in MAIN thread.");
      }
      if (isDoing()){//Already doing
          return;
      }
      mOnTaskUpdate=update;
      mProgress=null;
      mResult=null;
      onExecute(networker);
  }

  public final int getStatusCode() {
      TaskStatus status=mStatus;
      return null!=status?status.mStatus:Status.IDLE;
    }

  public final boolean isFinished(){
      return Status.FINISH==getStatusCode();
  }

  public final boolean isFinishSucceed(){
      return isFinished()&&null!=mResult;
  }

  protected void onPaused(boolean current,boolean last,String debug){
    //Do nothing
  }

  public final boolean pause(Boolean pause,String debug){
        final boolean curr=mPaused;
        if (null==pause){
            return curr;
        }else if (curr!=pause){
            mPaused=pause;
            onPaused(pause,curr,debug);
            return true;
        }
        return false;
  }

  protected final boolean notifyStatus(int status, String note){
      return notifyStatus(status,What.WHAT_NONE,note);
  }

  protected final boolean notifyStatus(int status, int what,String note){
     return notifyStatus(status,what, note,null);
  }

  protected final boolean notifyStatus(int status,String note,Object arg){
        return notifyStatus(status,What.WHAT_NONE,note,arg);
    }

  protected final boolean notifyStatus(int status,int what,String note,Object arg){
        if (isFinished()){
            Debug.W("Drop notify task status which task already finished."+this);
            return false;
        }
        mStatus.updateStatus(status,what,note);
        OnTaskUpdate taskUpdate=mOnTaskUpdate;
        if (null!=taskUpdate){
            taskUpdate.onTaskUpdate(status,what,note,arg,this);
        }
        boolean finished=status==Status.FINISH;
        if (finished){//Clean while task finish
            mOnTaskUpdate=null;
        }
        if (null!=arg){
            if (finished && arg instanceof Result){
                mResult=(Result)arg;
            }
            if (arg instanceof Progress){
                mProgress=(Progress)arg;
            }
        }
        return false;
  }

  public String getName() {
        return mName;
    }

  public static class TaskStatus {
        private int mStatus=Status.IDLE;
        private int mWhat;
        private String mNote;

        private void updateStatus(int status,int what,String note){
            mStatus=status;
            mWhat=what;
            mNote=note;
        }

        public int getStatus() {
            return mStatus;
        }

        public int getWhat() {
            return mWhat;
        }


        public String getNote() {
            return mNote;
        }
    }

}
