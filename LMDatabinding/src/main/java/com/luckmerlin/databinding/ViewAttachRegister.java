package com.luckmerlin.databinding;

import android.view.View;

public final class ViewAttachRegister {

    public final boolean register(View view,Runnable detachRunnable){
        if (null!=view&&view.getWindowToken()!=null&&null!=detachRunnable){
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                            view.removeOnAttachStateChangeListener(this);
                            detachRunnable.run();
                    }
                });
                return true;
        }
        return false;
    }
}
