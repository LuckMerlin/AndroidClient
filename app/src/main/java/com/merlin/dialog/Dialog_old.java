package com.merlin.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class Dialog_old implements View.OnClickListener{
    private final android.app.Dialog mDialog;

    public interface Callback{

    }

    public Dialog_old(Context context){
        android.app.Dialog dialog=mDialog=new android.app.Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window=dialog.getWindow();
//        window.setType((WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0f;
        window.setAttributes(params);
    }

    public final Dialog_old setContentView(View view){
        return null!=view?setContentView(view,null):this;
    }

    public final Dialog_old setContentView(int layoutId){
        Context context=getContext();
        View view=null!=context? LayoutInflater.from(context).inflate(layoutId,null):null;
        return null!=view?setContentView(view,null):this;
    }

    public final Dialog_old setContentView(View view, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            params=null!=params?params:new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(view,params);
            mDialog.getWindow().setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return this;
    }

    public final Dialog_old setCanceledOnTouchOutside(boolean flag){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCanceledOnTouchOutside(flag);
        }
        return this;
    }

    public final Dialog_old setCancelable(boolean flag){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setCancelable(flag);
        }
        return this;
    }

    public final boolean isShowing(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog&&dialog.isShowing();
    }

    public final<T extends View> T findViewById(int id,Class<T> cls){
        android.app.Dialog dialog=mDialog;
        View decorView=null!=dialog?dialog.getWindow().getDecorView():null;
        return null!=decorView?(T)decorView.findViewById(id):null;
    }

    protected final String getText(Object object){
        if (null!=object){
            if (object instanceof String){
                return (String)object;
            }else if (object instanceof Integer){
                Context context=getContext();
                return null!=context?context.getResources().getString((Integer)object):null;
            }
        }
        return null;
    }

    protected void onShow(){
        //Do nothing
    }

    public final Dialog_old show(){
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

    public final Dialog_old dismiss(){
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

    @Override
    public void onClick(View v) {
        //DO nothing
    }
}
