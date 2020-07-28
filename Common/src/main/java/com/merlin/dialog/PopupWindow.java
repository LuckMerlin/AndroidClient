package com.merlin.dialog;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.merlin.click.Clicker;
import com.merlin.lib.R;

public class PopupWindow {
    private final android.widget.PopupWindow mWindow=new android.widget.PopupWindow();
    private Drawable mBackground;
    public final static int DISMISS_OUT_MASK=0x01;//0001
    public final static int DISMISS_INNER_MASK=0x02;//0010
    private int mDismissFlag=0;
    private Integer mAppliedDismissFlag;

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

    public final boolean setDismissFlag(int flag){
        if (flag!=mDismissFlag){
            mDismissFlag=flag;
            applyDismissFlag(flag);
            return true;
        }
        return false;
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

    public final boolean setWidth(int width){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&window.getWidth()!=width){
            window.setWidth(width);
            return true;
        }
        return false;
    }

    public final boolean setHeight(int height){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&window.getHeight()!=height){
            window.setHeight(height);
            return true;
        }
        return false;
    }

    public boolean showAtLocation(View parent, int flag){
        return showAtLocation(parent, Gravity.CENTER,0,0,flag);
    }

    public boolean showAtLocation(View parent, int gravity, int x, int y,Integer dismissFlag){
        return showAtLocation(parent,gravity,x,y,null,dismissFlag);
    }

    public boolean showAtLocation(View parent, int gravity, int x, int y,View interrupter) {
        return showAtLocation(parent,gravity,x,y,interrupter,mDismissFlag);
    }

    public boolean showAtLocation(View parent, int gravity, int x, int y,Object interrupter,Integer dismissFlag){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=parent) {
            View contentView=null!=interrupter?getContentView():null;
            if (null!=contentView){
                Clicker.setInterrupterTag(contentView,interrupter,true);
            }
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAtLocation(parent, gravity, x, y);
            return true;
        }
        return false;
    }

    public boolean showAsDropDown(View anchor, int x, int y,Object interrupter,Integer dismissFlag) {
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=anchor) {
            View contentView=null!=interrupter?getContentView():null;
            if (null!=contentView){
                Clicker.setInterrupterTag(contentView,interrupter,true);
            }
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAsDropDown(anchor,x,y);
            return true;
        }
        return false;
    }

    public boolean setContentView(View view){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=view&&null==view.getParent()){
            Drawable drawable=mBackground;
//            drawable=null!=drawable?drawable:view.getContext().getResources()
//                    .getDrawable(R.drawable.round_corner_black);
            window.setBackgroundDrawable(drawable);
            window.setContentView(view);
            applyDismissFlag(mDismissFlag);
            return true;
        }
        return false;
    }

    public final void setOutsideTouchable(boolean touchable){
       setDismissFlag(touchable?(mDismissFlag|DISMISS_OUT_MASK):(mDismissFlag&~DISMISS_OUT_MASK));
    }

    public final View getContentView(){
        android.widget.PopupWindow window=mWindow;
        return null!=window?window.getContentView():null;
    }

    public final boolean dismiss(){
        android.widget.PopupWindow window=mWindow;
        if (null!=window){
            window.dismiss();
            return true;
        }
        return false;
    }

    public final boolean isShowing(){
        return null!=getContentView();
    }
}
