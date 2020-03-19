package com.merlin.transport;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import java.util.List;

public abstract class Convey<T extends Convey> implements What {
   private Canceler mCanceler=null;
   private final long mTotal;
   private long mConveyed;
   private List<T> mChildren;
   private final String mName;
   private Reply mReply;

   public Convey(String name,long total,long conveyed){
       mName=name;
       mTotal=total;
       mConveyed=conveyed;
   }

   protected boolean onPrepare(String debug){
        return false;
   }

   public final boolean cancel(boolean cancel,String debug){
       return false;
   }

   protected abstract Canceler onStart(Finish finish,String debug);

   public final Canceler start(String debug){
       if (!onPrepare(debug)){
           finish(WHAT_ARGS_INVALID,"Prepare fail.",null);
           return null;
       }
       Finish finish=new Finish();
       return onStart(finish,debug);
   }

   public boolean addChild(T convey,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't add NULL as child convey "+(null!=debug?debug:"."));
            return false;
        }
        if (isExistChild(convey)){
            Debug.W(getClass(),"Can't add already exist child convey "+(null!=debug?debug:"."));
            return false;
        }
        return false;
   }

   protected final boolean finish(int what,String note,Object data){

       return false;
   }

   public final boolean isFinished(){
       return null!=mReply;
   }

   public final Reply getReply(){
       return mReply;
   }

   public boolean remove(T convey,String debug){
       List<T> children=null!=convey?mChildren:null;
       if (null!=children&&children.remove(convey)){
           Debug.D(getClass(),"Remove convey child "+(null!=debug?debug:"."));
           return true;
       }
       return false;
   }

    public final int childCount(){
        List<T> children=mChildren;
        return null!=children?children.size():-1;
    }

    public final T findChild(Object obj){
        List<T> children=null!=obj?mChildren:null;
        int index=null!=children&&children.size()>0?children.indexOf(obj):-1;
        return index>=0?children.get(index):null;
    }

   public final boolean isExistChild(Object data){
       return null!=data&&null!=findChild(data);
   }

   public final boolean isCanceled() {
        Canceler canceler=mCanceler=null;
        return null!=canceler&&canceler.isCanceled();
    }

    public static class Finish{

    }

}
