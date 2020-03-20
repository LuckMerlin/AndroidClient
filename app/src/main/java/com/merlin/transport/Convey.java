package com.merlin.transport;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

public abstract class Convey implements What {
   public final static int IDLE=12313;
   public final static int PREPARING=12314;
   public final static int PREPARED=12315;
   public final static int STARTED=12316;
   public final static int PAUSED=12317;
   public final static int CANCELED=12318;
   public final static int FINISHED=12319;
   private final String mName;
   private Status mStatus;

   public Convey(String name){
       mName=name;
   }

   protected Reply onPrepare(String debug){
        return null;
   }

   public final boolean cancel(boolean cancel,String debug){
       return false;
   }

   protected abstract Reply onStart(Finish finish,String debug);

   public final Reply start(Finish finish,String debug){
       Status statusObj=getStatus();
       int status=null!=statusObj?statusObj.mStatus:IDLE;
       if (status!=IDLE&&status!=FINISHED&&status!=CANCELED&&status!=PAUSED){
           Debug.W(getClass(),"Can't start convey again while in status "+(null!=debug?debug:".")+" status="+status);
            return null;
       }
       notifyChangeStatus(PREPARING,null);
       Reply reply=onPrepare(debug);
       notifyChangeStatus(PREPARED,null);
       if (null!=reply&&!reply.isSuccess()){ //Prepare fail
           notifyChangeStatus(FINISHED,reply);
           return null;
       }
       final Finish innerFinish= (innerReply)->{
           mStatus=new Status(FINISHED,innerReply);
           if (null!=finish){
               finish.onFinish(innerReply);
           }
       };
       final Reply startReply= onStart(innerFinish,debug);
       if (null!=startReply&&!startReply.isSuccess()){
           innerFinish.onFinish(startReply);
       }
       return startReply;
   }

   private void notifyChangeStatus(int status,Object arg){
       mStatus=new Status(status,arg);
   }

   protected void notifyProgress(){

   }

   protected final boolean finish(int what,String note,Object data){

       return false;
   }

   public final boolean isFinished(){
       return isStatus(FINISHED);
   }

   public final Reply getReply(){
       Object finished=getStatusObject(FINISHED);
       return null!=finished&&finished instanceof Reply?(Reply)finished:null;
   }

    public final String getName() {
        return mName;
    }

    public final Object getStatusObject(int status){
        Status statusObj=getStatus();
        return null!=statusObj&&statusObj.mStatus==status?statusObj.mObject:null;
    }

   public final boolean isStatus(int status){
       Status statusObj=mStatus;
       return null!=statusObj&&statusObj.mStatus==status;
   }

   public final Status getStatus(){
       return mStatus;
   }

   public final boolean isCanceled() {
        return isStatus(CANCELED);
    }

    public interface Finish{
        void onFinish(Reply reply);
    }

   //mTotal=total;
   //mConveyed=conveyed;

    private static final class Status {
        private final int mStatus;
        private final Object mObject;

        public Status(int status,Object object){
            mStatus=status;
            mObject=object;
        }
    }

}
