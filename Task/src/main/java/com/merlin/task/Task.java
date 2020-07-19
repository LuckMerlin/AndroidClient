package com.merlin.task;

import android.os.Looper;

import com.task.debug.Debug;

import java.util.WeakHashMap;

public abstract class Task implements Status{
  private final TaskStatus mStatus=new TaskStatus();
  private WeakHashMap<OnTaskUpdate,Long> mReference;
  private Result mResult;
  private Canceler mCanceler;
  private Progress mProgress;
  private OnTaskUpdate mOnTaskUpdate;

  public final TaskStatus getStatus(){
        return mStatus;
  }

  public final Progress getProgress() {
        return mProgress;
  }

  public final Result getResult() {
      return mResult;
  }

  public final boolean isCanceled(){
      Canceler canceler=mCanceler;
      return null!=canceler&&canceler.cancel(null,null);
  }

  public final boolean cancel(Boolean cancel,String debug){
      Canceler canceler=mCanceler;
      return null!=canceler&&canceler.cancel(cancel,debug);
  }

  protected abstract Canceler onExecute(Networker networker);

  public final boolean isDoing(){
      int code=getStatusCode();
      return code!=Status.FINISH&code!=Status.IDLE;
  }

  public final Canceler execute(Networker networker,OnTaskUpdate update){
      Looper mainLooper=Looper.getMainLooper();
      Thread mainThread=null!=mainLooper?mainLooper.getThread():null;
      Thread currentThread=Thread.currentThread();
      if (null!=mainThread&&(null!=currentThread&&currentThread==mainThread)){
          throw new IllegalThreadStateException("Task can't execute in MAIN thread.");
      }
      if (isDoing()){//Already doing
          return null;
      }
      mOnTaskUpdate=update;
      mCanceler=null;
      mProgress=null;
      mResult=null;
      return mCanceler=onExecute(networker);
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
            mCanceler=null;
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
