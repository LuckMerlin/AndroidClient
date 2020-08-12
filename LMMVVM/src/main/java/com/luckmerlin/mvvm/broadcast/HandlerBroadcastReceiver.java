package com.luckmerlin.mvvm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class HandlerBroadcastReceiver extends BroadcastReceiver {
    private final Handler mHandler;
    private List<Intent> mDithering;

    public HandlerBroadcastReceiver(){
        this(null);
    }

    public HandlerBroadcastReceiver(Handler handler){
        mHandler=handler;
    }

    protected void onReceive(Intent intent,Context context){
        //Do nothing
    }

    protected Integer onResolveBroadcastDitherDuration(Intent intent){
        return null;
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (null!=intent){
            Handler handler=mHandler;
            if (null!=handler) {
                List<Intent> dithering=mDithering;
                if(null!=dithering&&dithering.contains(intent)){
                    return;//Give up dither broadcast
                }
                Integer duration=onResolveBroadcastDitherDuration(intent);
                if ((duration=duration==null?0:duration)>0){
                    dithering=null!=dithering?dithering:(mDithering=new ArrayList<>(1));
                    dithering.add(intent);
                }
                handler.postDelayed(()->onReceive(intent, context),duration<=0?0:duration);
            }else{
                onReceive(intent, context);
            }
        }
    }
}
