package com.merlin.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;

public class Dialog {
    private final android.app.Dialog mDialog;

    public Dialog(Context context){
        mDialog=new android.app.Dialog(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public final Dialog setContentView(View view){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view){
            dialog.setContentView(view);
        }
        return this;
    }

    public final Dialog setContentView(int layoutId){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setContentView(layoutId);
        }
        return this;
    }

    public final Dialog setContentView(View view, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.setContentView(view,params);
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

    public final View findViewById(int id){
        android.app.Dialog dialog=mDialog;
        View decorView=null!=dialog?dialog.getWindow().getDecorView():null;
        return null!=decorView?decorView.findViewById(id):null;
    }

    public final Dialog show(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            dialog.show();
        }
        return this;
    }

    public final Dialog dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&dialog.isShowing()){
            dialog.dismiss();
        }
        return this;
    }

}
