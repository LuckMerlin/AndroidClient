package com.merlin.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.merlin.client.R;

public class PopupWindow {
    private final android.widget.PopupWindow mWindow=new android.widget.PopupWindow();
    private Drawable mBackground;

    public PopupWindow(boolean touchable){
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(touchable);
        android.widget.PopupWindow window=mWindow;
        window.setOnDismissListener(()->{
            View view=window.getContentView();
            ViewParent parent=null!=view?view.getParent():null;
            if (null!=parent&&parent instanceof ViewGroup){
                ((ViewGroup)parent).removeView(view);
            }
        });
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

    public boolean showAtLocation(View parent, int gravity, int x, int y){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=parent) {
            window.showAtLocation(parent, gravity, x, y);
            return true;
        }
        return false;
    }

    public boolean setContentView(View view){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=view&&null==view.getParent()){
            Drawable drawable=mBackground;
            drawable=null!=drawable?drawable:view.getContext().getResources()
                    .getDrawable(R.drawable.round_dark_corner_small_normal);
            window.setBackgroundDrawable(drawable);
            window.setContentView(view);
            return true;
        }
        return false;
    }

    public final void setOutsideTouchable(boolean touchable){
        android.widget.PopupWindow window=mWindow;
        if (null!=window){
            window.setOutsideTouchable(touchable);
        }
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
