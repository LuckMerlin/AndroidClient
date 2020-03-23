package com.merlin.transport;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

public abstract class Convey extends ConveyStatus implements What {
   private OnConveyStatusChange mStatusChange;
   private long mTotal,mConveyed;
   private float mSpeed;
   private final String mName;
   private boolean mCancel=false;

   public Convey(String name){
        this(name,null);
    }

   public Convey(String name,OnConveyStatusChange change){
        super(IDLE,null);
       mStatusChange=change;
       mName=name;
   }

   protected abstract Reply onPrepare(String debug);

   protected  abstract Boolean onCancel(boolean cancel,String debug);

   public final boolean cancel(boolean cancel,String debug){
       boolean canceled=isStatus(CANCELED);
       if ((cancel&&canceled)||(!cancel&&!canceled)){
            return false;
       }
       mCancel=cancel;
       Boolean onCancel=onCancel(cancel,debug);
       if (null!=onCancel&&onCancel){
            updateStatus(CANCELED,null);
            return true;
       }
       return false;
   }

   protected abstract Reply onStart(Finisher finish,String debug);

   public final Reply start(Finisher finisher,OnConveyStatusChange change,String debug){
       int status=getStatus();
       if (status!=ConveyStatus.IDLE&&status!=ConveyStatus.FINISHED&&status!=ConveyStatus.CANCELED&&status!=ConveyStatus.PAUSED){
           Debug.W(getClass(),"Can't start convey again while in status "+(null!=debug?debug:".")+" status="+status);
            return null;
       }
       notifyChangeStatus(ConveyStatus.PREPARING,null,change);
       Reply reply=onPrepare(debug);
       notifyChangeStatus(ConveyStatus.PREPARED,null,change);
       if (null!=reply&&!reply.isSuccess()){ //Prepare fail
           notifyChangeStatus(ConveyStatus.FINISHED,reply,change);
           return null;
       }
       final Finisher innerFinish=new Finisher() {
           @Override
           public void onFinish(Reply innerReply) {
               notifyChangeStatus(ConveyStatus.FINISHED,innerReply,change);
               if (null!=finisher){
                   finisher.onFinish(innerReply);
               }
           }

           @Override
           public void onProgress(long conveyed, long total, float speed,Convey convey) {
               mConveyed=conveyed;mTotal=total;mSpeed=speed;
               if (null!=finisher){
                   finisher.onProgress(conveyed,total,speed,convey);
               }
               notifyChangeStatus(ConveyStatus.PROGRESS,convey,change);
           }
       };
       notifyChangeStatus(ConveyStatus.STARTED,null,change);
       final Reply startReply= onStart(innerFinish,debug);
       if (null!=startReply&&!startReply.isSuccess()){
           innerFinish.onFinish(startReply);
           notifyChangeStatus(ConveyStatus.FINISHED,startReply,change);
       }
       return startReply;
   }

   private void notifyChangeStatus(int status,Object arg,OnConveyStatusChange change){
       if (status!=ConveyStatus.PROGRESS){
           updateStatus(status,arg);
       }
       if (null!=change){
           change.onConveyStatusChanged(status,this,arg);
       }
       OnConveyStatusChange globalChange=mStatusChange;
       if (null!=globalChange){
           globalChange.onConveyStatusChanged(status,this,arg);
       }
   }

   public final void setStatusChange(OnConveyStatusChange statusChange){
       mStatusChange=statusChange;
   }

   public final OnConveyStatusChange getStatusChange() {
        return mStatusChange;
    }

   public final boolean isFinished(){
       return isStatus(ConveyStatus.FINISHED);
   }

    public boolean isSuccessFinished(){
        return isReply(true,What.WHAT_SUCCEED);
    }

    public final boolean isReply(Boolean succeed,Integer what){
        Reply reply=getReply();
        if (null!=reply){
            return (null==succeed||succeed==reply.isSuccess())&&(null==what||what==reply.getWhat());
        }
        return false;
    }

   public final Reply getReply(){
       Object finished=getStatusObject(ConveyStatus.FINISHED);
       return null!=finished&&finished instanceof Reply?(Reply)finished:null;
   }

   public final String getName() {
        return mName;
    }

   public float getSpeed() {
        return mSpeed;
    }

   public long getConveyed() {
        return mConveyed;
    }

   public long getTotal() {
        return mTotal;
    }

    protected final boolean isCancel(){
       return mCancel;
    }

   public final boolean isCanceled() {
        return isStatus(ConveyStatus.CANCELED);
    }

   public interface Finisher{
        void onFinish(Reply reply);
        void onProgress(long conveyed,long total,float speed, Convey convey);
    }

   @Override
   public String toString() {
        return ""+getName()+" " +
                ""+super.toString();
    }
}
