package com.merlin.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class Dialog {
    private final android.app.Dialog mDialog;

    public Dialog(Context context){
        mDialog=new android.app.Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window=mDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public final Dialog setContentView(View view){
        return null!=view?setContentView(view,null):this;
    }

    public final Dialog setContentView(int layoutId){
        Context context=getContext();
        View view=null!=context? LayoutInflater.from(context).inflate(layoutId,null):null;
        return null!=view?setContentView(view,null):this;
    }

    public final Dialog setContentView(View view, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            params=null!=params?params:new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(view,params);
            mDialog.getWindow().setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return this;
    }

    public final Dialog setCanceledOnTouchOutside(boolean flag){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCanceledOnTouchOutside(flag);
        }
        return this;
    }

    public final Dialog setCancelable(boolean flag){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCancelable(flag);
        }
        return this;
    }

    public final<T extends View> T findViewById(int id,Class<T> cls){
        android.app.Dialog dialog=mDialog;
        View decorView=null!=dialog?dialog.getWindow().getDecorView():null;
        return null!=decorView?(T)decorView.findViewById(id):null;
    }

    protected void onShow(){
        //Do nothing
    }

    public final Dialog show(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            dialog.show();
            onShow();
        }
        return this;
    }

    protected void onDismiss(){
        //Do nothing
    }

    public final Dialog dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&dialog.isShowing()){
            dialog.dismiss();
            onDismiss();
        }
        return this;
    }

    public final Context getContext(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getContext():null;
    }

}
