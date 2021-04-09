package com.luckmerlin.databinding.dialog;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.luckmerlin.core.debug.Debug;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @deprecated
 */
public class ClickDispatcher implements View.OnTouchListener {
    private int mLongClickDuration;
    private final Map<View,Touch> mTouches=new WeakHashMap();

    void onViewClick(View view, long downTime, MotionEvent downEvent){

    }
    void onViewLongClick(View view, long downTime, MotionEvent downEvent){
        //Do nothing
    }

    public final ClickDispatcher enableLongClick() {
        return setLongClickTimeout(2*1000);
    }

    public final ClickDispatcher setLongClickTimeout(int duration) {
        this.mLongClickDuration = duration;
        return this;
    }

    @Override
    public final boolean onTouch(View v, MotionEvent event) {
        if (null==event||null==v){
            return false;
        }
        Debug.D("DDDDDDDd DDDDDDDD  "+v);
        Touch touch=null;
        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                touch=new Touch(v,mLongClickDuration);
                mTouches.put(v,touch);
                touch.onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touch=mTouches.get(v);
                if (null!=touch){
                    touch.onTouchMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch=mTouches.remove(v);
                if (null!=touch){
                    touch.onTouchFinish(event);
                    if (action==MotionEvent.ACTION_UP){
                        if (null!=touch.mLongClick){
                            ClickDispatcher.this.onViewLongClick(touch.mView,touch.mDownTime,touch.mDownEvent);
                        }
                        if (null!=touch.mClick){
                            ClickDispatcher.this.onViewClick(touch.mView,touch.mDownTime,touch.mDownEvent);
                        }
                    }
                }
                break;
        }
        return true;
    }

    private static class Touch{
        private MotionEvent mDownEvent;
        private long mDownTime=0;
        private final View mView;
        private final int mLongClickDuration;
        private Runnable mLongClick;
        private Runnable mClick;

        private Touch(View view,int longClickDuration){
            mView=view;
            mLongClickDuration=longClickDuration;
        }

        private void onTouchDown(MotionEvent event){
            mDownEvent=event;
            mDownTime=System.currentTimeMillis();
            final Runnable longClick=mLongClick=new Runnable() {
                @Override
                public void run() {
                    Runnable current=mLongClick;
                    if (null!=current&&current==this){
                        mLongClick=null;
                    }
                    View view=mView;
                    if (null!=view){
                        view.removeCallbacks(this);
                    }
                }
            };
            final Runnable click=mClick=new Runnable() {
                @Override
                public void run() {
                    Runnable current=mClick;
                    if (null!=current&&current==this){
                        mClick=null;
                    }
                    View view=mView;
                    if (null!=view){
                        view.removeCallbacks(this);
                    }
                }
            };
            View view=mView;
            if (null!=view){
                if (mLongClickDuration>0){
                    view.postDelayed(longClick,mLongClickDuration);
                }
                view.postDelayed(click, ViewConfiguration.getTapTimeout());
            }
        }

        private void onTouchMove(MotionEvent event){
            MotionEvent down=mDownEvent;
            if(null!=down&&(Math.abs(down.getX() - event.getX()) > 20 || Math.abs(down.getY() - event.getY()) >20)){
                remove(mView,mLongClick,mClick);
            }
        }

        private void onTouchFinish(MotionEvent event){
            remove(mView,mLongClick,mClick);
        }

        private void remove(View view,Runnable ...runnables){
            if (null!=view&&null!=runnables){
                for (Runnable child:runnables) {
                    if (null!=child){
                        view.removeCallbacks(child);
                    }
                }
            }
        }
    }
}
