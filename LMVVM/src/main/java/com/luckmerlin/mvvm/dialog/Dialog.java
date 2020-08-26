package com.luckmerlin.mvvm.dialog;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.mvvm.Model;

public final class Dialog {
    private final android.app.Dialog mDialog;

    public Dialog(Context context,Integer windowType){
        android.app.Dialog dialog=mDialog=null!=context?new android.app.Dialog(context):null;
        if (null!=dialog){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window=dialog.getWindow();
            if (null!=window){
                if (null!=windowType){
                    window.setType(windowType);
                }
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public Dialog(android.app.Dialog dialog){
        mDialog=dialog;
    }

    public Window getWindow(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getWindow():null;
    }

    public final Dialog setDimAmount(float dimAmount){
        Window window=getWindow();
        if (null!=window){
            window.setDimAmount(dimAmount);
        }
        return this;
    }

    public final Dialog setContentView(int layoutId,boolean recreate){
        Context context=getContext();
        if (null!=context){
            LayoutInflater inflater=LayoutInflater.from(context);
            ViewDataBinding binding= DataBindingUtil.inflate(inflater,layoutId,null,false);
            if (null!=binding){
                return setContentView(binding,recreate);
            }
            return setContentView(inflater.inflate(layoutId,null,false),recreate);
        }
        return this;
    }

    public final Dialog setContentView(ViewDataBinding binding, boolean recreate){
        return setContentView(binding,recreate,null);
    }

    public final Dialog setContentView(ViewDataBinding binding, boolean recreate,int[] padding){
        View view=null!=binding?binding.getRoot():null;
        return null!=view?setContentView(view,recreate,padding):this;
    }

    public final Dialog setContentView(View view,boolean recreate){
        return setContentView(view,recreate,null);
    }

    public final Dialog setContentView(View view,boolean recreate,int[] padding){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view&&null==view.getParent()){
//            if (!isCreated()||recreate) {
//                create(padding);
//            }
//            DialogLayoutBinding binding=mBinding;
//            if (null!=binding){
//                binding.setContentLayout(view);
//            }
        }
        return this;
    }

    public final Dialog title(Object title){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setTitleText(title);
//        }
        return this;
    }

    public final Dialog message(Object msgTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setMessageText(msgTextId);
//        }
        return this;
    }

    public final Dialog left(Object leftTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setLeftText(leftTextId);
//        }
        return this;
    }

    public final Dialog center(Object centerTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setCenterText(centerTextId);
//        }
        return this;
    }

    public final Dialog right(Object rightTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setRightText(rightTextId);
//        }
        return this;
    }

    public final boolean show(){
        return show(null,true);
    }

    public final boolean show(OnViewClick click){
        return show(click,null!=click&&(click instanceof Model ||click instanceof Activity ||
                click instanceof Service ||click instanceof BroadcastReceiver));
    }

    public final boolean show(OnViewClick click, boolean weak){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
//            DialogLayoutBinding binding=mBinding;
//            View root=null!=binding?binding.getRoot():null;
//            if (null!=root){
//                Clicker.setInterrupterTag(root,click,weak);
//            }
            dialog.show();
            return true;
        }
        return false;
    }

    public final Dialog dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&dialog.isShowing()){
            dialog.dismiss();
        }
        return this;
    }

    public final<T extends View> T findViewById(int id,Class<T> cls){
        android.app.Dialog dialog=mDialog;
        View decorView=null!=dialog?dialog.getWindow().getDecorView():null;
        return null!=decorView?(T)decorView.findViewById(id):null;
    }

    public final String getViewText(int viewId,String def){
        View view=findViewById(viewId,View.class);
        if (null!=view&&view instanceof TextView){
            return ""+((TextView)view).getText();
        }
        return def;
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

    public WindowManager.LayoutParams getAttributes(){
        Window window=getWindow();
       return null!=window?window.getAttributes():null;
    }

    public Dialog setAttributes(WindowManager.LayoutParams params){
        Window window=getWindow();
        if (null!=params){
            window.setAttributes(params);
        }
        return this;
    }

    public final int getGravity(int def){
        WindowManager.LayoutParams params=getAttributes();
        return null!=params?params.gravity:def;
    }

    public final Dialog gravity(int gravity){
        Window window=getWindow();
        if (null!=window){
            window.setGravity(gravity);
        }
        return this;
    }

    public final Context getContext(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getContext():null;
    }

    public final View getRootView() {
//        ViewDataBinding binding=mBinding;
//        return null!=binding?binding.getRoot():null;
        return null;
    }


}
