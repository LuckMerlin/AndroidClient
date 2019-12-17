package com.merlin.pop;

import android.view.View;

public class PopupWindow {
    private final android.widget.PopupWindow mWindow=new android.widget.PopupWindow();

    public boolean showAtLocation(View parent, int gravity, int x, int y){
        android.widget.PopupWindow window=mWindow;
        if (null!=window&&null!=parent) {
            window.showAtLocation(parent, gravity, x, y);
            return true;
        }
        return false;
    }
}
