package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelBinder;

/**
 * @deprecated
 */
public final class PopupWindow implements PublishMethods, PublishFields {
    private final android.widget.PopupWindow mWindow=new android.widget.PopupWindow();
    private Drawable mBackground;
    public final static int DISMISS_NONE=0x00;//0000
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
        setDismissFlag(mDismissFlag|(touchable?DISMISS_OUT_MASK:DISMISS_NONE));
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

    public final PopupWindow setSize(Integer width,Integer height){
        if (null!=width){
            setWidth(width);
        }
        if (null!=height){
            setHeight(height);
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

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y) {
        return showAtLocation(parent,gravity,x,y,mDismissFlag);
    }

    public PopupWindow showAtLocation(View parent, int gravity, int x, int y,Integer dismissFlag){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=parent) {
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAtLocation(parent, gravity, x, y);
        }
        return this;
    }

    public final PopupWindow showAsDropDown(View anchor, int x, int y,Integer dismissFlag) {
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=anchor) {
            if (null!=dismissFlag){
                applyDismissFlag(dismissFlag);
            }
            window.showAsDropDown(anchor,x,y);
        }
        return this;
    }

    public final PopupWindow showAtLocation(View parent, Object rootObject, int gravity, int x, int y, Integer dismissFlag){
        if (setContentViewInner(rootObject)){
            dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
            showAtLocation(parent, gravity, x, y, dismissFlag);
        }
        return this;
    }

    public PopupWindow setContentView(View view){
        setContentViewInner(view);
        return this;
    }

    public PopupWindow setContentView(Context context,Model model){
        if (null!=model){
            MatchBinding matchBinding=null!=model?new ModelBinder().bindModelForObject(
                    context,model,"Before show view at location."):null;
            if (null!=matchBinding){
                setContentViewInner(matchBinding);
            }
        }
        return this;
    }

    private boolean setContentViewInner(Object rootObject){
        if (null==rootObject){
            return false;
        }
        android.widget.PopupWindow window=mWindow;
        if (rootObject instanceof View&&(null==((View)rootObject).getParent())){
            Drawable drawable=mBackground;
            window.setBackgroundDrawable(drawable);
            window.setContentView((View)rootObject);
            applyDismissFlag(mDismissFlag);
            return true;
        }else if (rootObject instanceof ViewDataBinding){
            ViewDataBinding binding=(ViewDataBinding)rootObject;
            return setContentViewInner(binding.getRoot());
        }
        return false;
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
        android.widget.PopupWindow window=mWindow;
        return null!=window&&window.isShowing();
    }
}
