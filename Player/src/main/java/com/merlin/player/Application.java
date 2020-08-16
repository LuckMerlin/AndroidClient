package com.merlin.player;


import android.graphics.PixelFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 新建悬浮窗控件
//        Button button = new Button(getApplicationContext());
//        button.setText("Floating Window");
//        button.setBackgroundColor(Color.BLUE);
        ViewDataBinding binding= DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.cao,null,false);
        // 设置LayoutParam
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = 500;
        layoutParams.height = 100;
        layoutParams.x = 300;
        layoutParams.y = 300;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 将悬浮窗控件添加到WindowManager
//        windowManager.addView(binding.getRoot(), layoutParams);
    }
}
