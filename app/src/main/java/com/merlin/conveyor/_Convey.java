//package com.merlin.conveyor;
//import com.merlin.api.Canceler;
//import com.merlin.api.Reply;
//import com.merlin.api.What;
//import com.merlin.server.Retrofit;
//import com.merlin.transport.OnConveyStatusChange;
//import com.merlin.transport.Status;
//
//public abstract class _Convey extends ConveyStatus implements What {
//   private OnConveyStatusChange mStatusChange;
//   private long mTotal,mConveyed;
//   private float mSpeed;
//   private final String mName;
//   private boolean mCancel=false;
//
//   public _Convey(String name){
//        this(name,null);
//    }
//
//   public _Convey(String name, OnConveyStatusChange change){
//        super(Status.IDLE,null);
//       mName=name;
//       setListener(change,"While new instance.");
//   }
//    /**
//     * @deprecated
//     */
//   protected void onPrepare(Conveyor.Step step, Retrofit retrofit, String debug){
//   }
//
//    /**
//     * @deprecated
//     */
//   protected Boolean onCancel(Retrofit retrofit,boolean cancel,String debug){
//       return null;
//   }
//    /**
//     * @deprecated
//     */
//   protected final boolean cancel(Retrofit retrofit,boolean cancel,String debug){
//       if (isStatus(Status.FINISHED)){
//           return false;
//       }
//       boolean canceled=isStatus(Status.CANCELED);
//       if ((cancel&&canceled)||(!cancel&&!canceled)){
//            return false;
//       }
//       mCancel=cancel;
//       Boolean onCancel=onCancel(retrofit,cancel,debug);
//       if (null!=onCancel&&onCancel){
//            updateStatus(Status.CANCELED,null);
//            return true;
//       }
//       return false;
//   }
//    /**
//     * @deprecated
//     */
//   protected void onStart(Conveyor.Step step, Retrofit retrofit, String debug){
//       return;
//   }
//
//   protected Canceler onStart(Retrofit retrofit,OnConveyStatusChange change,String debug){
//
//       return null;
//   }
//
//   protected final Canceler start(Retrofit retrofit, OnConveyStatusChange change, String debug){
//         return onStart(retrofit,change,debug);
////       int status=getStatus();
////       if (status!=ConveyStatus.IDLE&&status!=ConveyStatus.FINISHED&&status!=ConveyStatus.CANCELED&&status!=ConveyStatus.PAUSED){
////           Debug.W(getClass(),"Can't start convey again while in status "+(null!=debug?debug:".")+" status="+status);
////           return;
////       }
////       notifyChangeStatus(ConveyStatus.PREPARING,null,change);
////       onPrepare(step,retrofit,debug);
////       notifyChangeStatus(ConveyStatus.PREPARED,null,change);
////       if (null!=reply&&!reply.isSuccess()){ //Prepare fail
////           notifyChangeStatus(ConveyStatus.FINISHED,reply,change);
////           return;
////       }
////       final Step innerFinish=new Finisher() {
////           @Override
////           public void onFinish(Reply innerReply) {
////               notifyChangeStatus(ConveyStatus.FINISHED,innerReply,change);
////               if (null!=finisher){
////                   finisher.onFinish(innerReply);
////               }
////           }
////
////           @Override
////           public void onProgress(long conveyed, long total, float speed,Convey convey) {
////               mConveyed=conveyed;mTotal=total;mSpeed=speed;
////               if (null!=finisher){
////                   finisher.onProgress(conveyed,total,speed,convey);
////               }
////               notifyChangeStatus(ConveyStatus.PROGRESS,convey,change);
////           }
////       };
////       notifyChangeStatus(ConveyStatus.STARTED,null,change);
////       final Reply startReply= onStart(step,retrofit,debug);
////       if (null!=startReply&&!startReply.isSuccess()){
//////           innerFinish.onFinish(startReply);
////           notifyChangeStatus(ConveyStatus.FINISHED,startReply,change);
////       }
////       return startReply;
////       return null;
//   }
//
//   private void notifyChangeStatus(int status,Object arg,OnConveyStatusChange change){
//       if (status!=ConveyStatus.PROGRESS){
//           updateStatus(status,arg);
//       }
////       if (null!=change){
////           change.onConveyStatusChanged(status,this,arg);
////       }
////       OnConveyStatusChange globalChange=mStatusChange;
////       if (null!=globalChange){
////           globalChange.onConveyStatusChanged(status,this,arg);
////       }
//   }
//
//   public final boolean setListener(OnConveyStatusChange statusChange,String debug){
//       mStatusChange=statusChange;
//       return false;
//   }
//
////    public final boolean removeListener(OnConveyStatusChange statusChange,String debug){
////        List<OnConveyStatusChange> list=null!=statusChange?mStatusChange:null;
////        if (null!=list){
////            synchronized (list){
////                return list.remove(statusChange);
////            }
////        }
////        return false;
////    }
//
//   public final boolean isFinished(){
//       return isStatus(ConveyStatus.FINISHED);
//   }
//
//    public boolean isSuccessFinished(){
//        return isReply(true,What.WHAT_SUCCEED);
//    }
//
//    public final boolean isReply(Boolean succeed,Integer what){
//        Reply reply=getReply();
//        if (null!=reply){
//            return (null==succeed||succeed==reply.isSuccess())&&(null==what||what==reply.getWhat());
//        }
//        return false;
//    }
//
//   public final Reply getReply(){
//       Object finished=getStatusObject(ConveyStatus.FINISHED);
//       return null!=finished&&finished instanceof Reply?(Reply)finished:null;
//   }
//
//   public final String getName() {
//        return mName;
//    }
//
//   public float getSpeed() {
//        return mSpeed;
//    }
//
//   public long getConveyed() {
//        return mConveyed;
//    }
//
//   public long getTotal() {
//        return mTotal;
//    }
//
//    protected final boolean isCancel(){
//       return mCancel;
//    }
//
//   public final boolean isCanceled() {
//        return isStatus(ConveyStatus.CANCELED);
//    }
//
//   public final float getProgress(){
//        long conveyed=mConveyed;
//        long total=mTotal;
//        return (float)(conveyed>=0&&total>0?((double)conveyed/total):0);
//   }
//
//    public final OnConveyStatusChange getStatusChange() {
//        return mStatusChange;
//    }
//
//    /**
//     * @deprecated
//     */
//    public interface Finisher{
//        void onFinish(Reply reply);
//        void onProgress(long conveyed,long total,float speed, _Convey convey);
//    }
//
//   @Override
//   public String toString() {
//        return ""+getName()+" "+super.toString();
//    }
//}
