package com.luckmerlin.databinding.dialog;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.luckmerlin.databinding.touch.TouchListener;

public class PopupWindow {
    private final android.widget.PopupWindow mWindow=new android.widget.PopupWindow();
    private Drawable mBackground;
    public final static int DISMISS_OUT_MASK=0x01;//0001
    public final static int DISMISS_INNER_MASK=0x02;//0010
    private int mDismissFlag=0;
    private Integer mAppliedDismissFlag;
    private TouchListener mTouchListener;

    public interface OnDismissListener{
        void onDismiss(PopupWindow popupWindow);
    }

    public PopupWindow(boolean touchable) {
        this(touchable,null);
    }

    public PopupWindow(boolean touchable, OnDismissListener listener){
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        android.widget.PopupWindow window=mWindow;
        window.setOnDismissListener(()->{
            mTouchListener=null;
            View view=window.getContentView();
            ViewParent parent=null!=view?view.getParent():null;
            if (null!=parent&&parent instanceof ViewGroup){
                ((ViewGroup)parent).removeView(view);
            }
            if (null!=listener){
                listener.onDismiss(this);
            }
        });
        setDismissFlag(mDismissFlag|(touchable?DISMISS_OUT_MASK:0));
    }

    public final PopupWindow setDismissFlag(int flag){
        if (flag!=mDismissFlag){
            mDismissFlag=flag;
            applyDismissFlag(flag);
        }
        return this;
    }

    private boolean applyDismissFlag(int flag){
        android.widget.PopupWindow window=mWindow;
        if (null!=window){
            if (null==mAppliedDismissFlag||(flag!=mAppliedDismissFlag)){
                mAppliedDismissFlag=flag;
                window.setOutsideTouchable((flag&DISMISS_OUT_MASK)>0);
                window.setTouchInterceptor(null);
                if ((flag&DISMISS_INNER_MASK)>0){
                    window.setTouchInterceptor(( v, event)->{
                        int action=event.getAction();
                        if (action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){
                            v.setVisibility(View.GONE);
                            v.postDelayed(()->dismiss(),200);
                        }
                        return false;
                    });
                }
                return true;
            }
        }
        return false;
    }

    public final PopupWindow setWidth(int width){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&window.getWidth()!=width){
            window.setWidth(width);
        }
        return this;
    }

    public final PopupWindow setHeight(int height){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&window.getHeight()!=height){
            window.setHeight(height);
        }
        return this;
    }

    public PopupWindow showAtLocation(View parent, int flag){
        return showAtLocation(parent, Gravity.CENTER,0,0,flag);
    }

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y,Integer dismissFlag){
        return showAtLocation(parent,gravity,x,y,null,dismissFlag);
    }

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y,TouchListener interrupter) {
        return showAtLocation(parent,gravity,x,y,interrupter,mDismissFlag);
    }

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y,TouchListener interrupter,Integer dismissFlag){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=parent) {
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAtLocation(parent, gravity, x, y);
            mTouchListener=null!=interrupter?interrupter:mTouchListener;
        }
        return this;
    }

    public PopupWindow showAsDropDown(View anchor, int x, int y,TouchListener interrupter,Integer dismissFlag) {
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=anchor) {
            View contentView=null!=interrupter?getContentView():null;
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAsDropDown(anchor,x,y);
            mTouchListener=null!=interrupter?interrupter:mTouchListener;
        }
        return this;
    }

    public PopupWindow setContentView(View view){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=view&&null==view.getParent()){
            Drawable drawable=mBackground;
            window.setBackgroundDrawable(drawable);
            window.setContentView(view);
            applyDismissFlag(mDismissFlag);
        }
        return this;
    }

    public PopupWindow setTouchListener(TouchListener touchListener) {
        this.mTouchListener = touchListener;
        return this;
    }

    public final PopupWindow setOutsideTouchable(boolean touchable){
       setDismissFlag(touchable?(mDismissFlag|DISMISS_OUT_MASK):(mDismissFlag&~DISMISS_OUT_MASK));
       return this;
    }

    public final View getContentView(){
        android.widget.PopupWindow window=mWindow;
        return null!=window?window.getContentView():null;
    }

    public final PopupWindow dismiss(){
        android.widget.PopupWindow window=mWindow;
        if (null!=window){
            window.dismiss();
        }
        return this;
    }

    public final boolean isShowing(){
        return null!=getContentView();
    }
}
