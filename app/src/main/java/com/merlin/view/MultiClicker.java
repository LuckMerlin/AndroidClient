package com.merlin.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.merlin.binding.IDs;
import com.merlin.client.R;
import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;

public final class MultiClicker {
    private Object mListener;

    public MultiClicker(OnMultiClickListener  listener,boolean weakListener){
        setListener(listener,weakListener);
    }

    public void setListener(OnMultiClickListener  listener,boolean weakListener){
        Object object=mListener;
        if (null!=object){
            mListener=null;
            if (object instanceof WeakReference){
                ((WeakReference)object).clear();
            }
        }
        if (null!=listener){
            mListener=weakListener?new WeakReference<>(listener):listener;
        }
    }

    public static MultiClick multi(Object arg){
        return multi(arg,false);
    }

    public static MultiClick multi(Object arg,boolean coverLister){
        return new MultiClick(arg,coverLister);
    }

    public static class MultiClick{
        private Object mArg;
        private boolean mCoverLister;

        public MultiClick(Object arg,boolean coverLister ){
            mArg=arg;
            mCoverLister=coverLister;
        }
    }

    public OnMultiClickListener getListener(){
        Object object=mListener;
        object=null!=object&&object instanceof WeakReference?((WeakReference)object).get():object;
        return null!=object&&object instanceof OnMultiClickListener?(OnMultiClickListener)object:null;
    }

    public boolean attach(View root,MultiClick click){
        return null!=root&&null!=click&&attach(root,new IDs(-1,click.mArg),click.mCoverLister);
    }

    public boolean attach(View view,IDs arg){
        return attach(view,arg,false);
    }

    public boolean attach(View root, IDs arg, boolean cover){
        if (null!=root&&(cover||!root.hasOnClickListeners())){
            final int maxInterval=400;
            final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                @Override
                public void run() {
                    int count=mClickCount;
                    mClickCount=0;//Reset
                    OnMultiClickListener listener=getListener();
                    Debug.D(getClass(),"&&&&&&&listener& "+listener);
                    if (null!=listener){
                        Object object=root.getTag(R.id.resourceId);
                        int resId;
                        if (null!=object&&object instanceof IDs){
                            resId=((IDs)object).getResourceId();
                            object=((IDs)object).getArg();
                        }else{
                            resId=root.getId();
                        }
                        Debug.D(getClass(),"&&&&&&&& "+object);
                        listener.onMultiClick(root,count,resId,object);
                    }
//                if (null==multiListener||!multiListener.
//                        onItemMultiClick(mView,count, getViewId(),position,data)){
//                    if (count==1&&null!=listener){
//                        listener.onItemClick(mView, mView.getId(),position, data);
//                    }
//                }
                }
            };
            root.setTag(R.id.resourceId,arg);
            root.setOnClickListener((view)->{
                Debug.D(getClass(),"&&&&&&&setOnClickListener& "+multiRunnable);
                view.removeCallbacks(multiRunnable);
                multiRunnable.mView=view;
                if (multiRunnable.mClickCount==0){
                    multiRunnable.mFirstTime=System.currentTimeMillis();
                }
                multiRunnable.mClickCount+=1;
                view.postDelayed(multiRunnable,maxInterval);
            });
            return true;
        }
        return false;
    }

    private static abstract class MultiClickRunnable implements Runnable{
        View mView;
        long mFirstTime;
        int mClickCount;
    }

}
