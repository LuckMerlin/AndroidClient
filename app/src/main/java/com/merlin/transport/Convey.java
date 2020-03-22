package com.merlin.transport;

import androidx.annotation.NonNull;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

public abstract class Convey implements What {
   private final String mName;
   private ConveyStatus mStatus;

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
       ConveyStatus statusObj=getStatus();
       int status=null!=statusObj?statusObj.getStatus():ConveyStatus.IDLE;
       if (status!=ConveyStatus.IDLE&&status!=ConveyStatus.FINISHED&&status!=ConveyStatus.CANCELED&&status!=ConveyStatus.PAUSED){
           Debug.W(getClass(),"Can't start convey again while in status "+(null!=debug?debug:".")+" status="+status);
            return null;
       }
       notifyChangeStatus(ConveyStatus.PREPARING,null);
       Reply reply=onPrepare(debug);
       notifyChangeStatus(ConveyStatus.PREPARED,null);
       if (null!=reply&&!reply.isSuccess()){ //Prepare fail
           notifyChangeStatus(ConveyStatus.FINISHED,reply);
           return null;
       }
       final Finish innerFinish= (innerReply)->{
           mStatus=new ConveyStatus(ConveyStatus.FINISHED,innerReply);
           notifyChangeStatus(ConveyStatus.FINISHED,mStatus);
           if (null!=finish){
               finish.onFinish(innerReply);
           }
       };
       notifyChangeStatus(ConveyStatus.STARTED,null);
       final Reply startReply= onStart(innerFinish,debug);
       if (null!=startReply&&!startReply.isSuccess()){
           innerFinish.onFinish(startReply);
       }
       return startReply;
   }

   private void notifyChangeStatus(int status,Object arg){
       mStatus=new ConveyStatus(status,arg);
       Debug.D(getClass(),"notifyChangeStatus "+status);
   }

   protected void notifyProgress(){

   }

   protected final boolean finish(int what,String note,Object data){

       return false;
   }

   public final boolean isFinished(){
       return isStatus(ConveyStatus.FINISHED);
   }

   public final Reply getReply(){
       Object finished=getStatusObject(ConveyStatus.FINISHED);
       return null!=finished&&finished instanceof Reply?(Reply)finished:null;
   }

    public final String getName() {
        return mName;
    }

    public final Object getStatusObject(int status){
        ConveyStatus statusObj=getStatus();
        return null!=statusObj&&statusObj.getStatus()==status?statusObj.getObject():null;
    }

   public final boolean isStatus(int status){
       ConveyStatus statusObj=mStatus;
       return null!=statusObj&&statusObj.getStatus()==status;
   }

   public final ConveyStatus getStatus(){
       return mStatus;
   }

   public final boolean isCanceled() {
        return isStatus(ConveyStatus.CANCELED);
    }

    public interface Finish{
        void onFinish(Reply reply);
    }

    @NonNull
    @Override
    public String toString() {
        return ""+getName()+" " +
                ""+super.toString();
    }
}
