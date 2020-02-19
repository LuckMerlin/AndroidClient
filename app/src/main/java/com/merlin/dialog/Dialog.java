package com.merlin.dialog;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.R;
import com.merlin.client.databinding.DialogLayoutBinding;
import com.merlin.debug.Debug;
import com.merlin.model.Model;
import com.merlin.view.Clicker;
import com.merlin.view.OnTapClick;

public class Dialog implements View.OnClickListener{
    private final android.app.Dialog mDialog;
    private DialogLayoutBinding mBinding;

    public interface Callback{

    }

    public Dialog(Context context){
        android.app.Dialog dialog=mDialog=new android.app.Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window=dialog.getWindow();
//        window.setType((WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        dialog.setOnDismissListener((dlg)->{
            DialogLayoutBinding binding=mBinding;
            mBinding=null;
            View root= null!=binding?binding.getRoot():null;
            if (null!=root){
                Clicker.setInterrupterTag(root,null);
                binding.unbind();
            }
        });
        params.dimAmount = 0f;
        window.setAttributes(params);
    }

    public final Dialog setContentView(int layoutId){
        Context context=getContext();
        if (null!=context){
            LayoutInflater inflater=LayoutInflater.from(context);
            ViewDataBinding binding=DataBindingUtil.inflate(inflater,layoutId,null,false);
            if (null!=binding){
                return setContentView(binding);
            }
            return setContentView(inflater.inflate(layoutId,null,false));
        }
        return this;
    }

    public final Dialog setContentView(ViewDataBinding binding){
        View view=null!=binding?binding.getRoot():null;
        return null!=view?setContentView(view):this;
    }

//    public final Dialog setVariable(int variableId, @Nullable Object value){
//        DialogLayoutBinding binding = mBinding;
//        null!=binding?binding.getContentLayout()
//        if (null!=binding){
//             binding.setVariable(variableId,value);
//        }
//        return this;
//    }

    public final Dialog create(){
        final android.app.Dialog dialog=mDialog;
        final Context context=null!=dialog?dialog.getContext():null;
        if (null!=dialog&&null!=context){
            DialogLayoutBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_layout,null,false);
            View root=null!=binding?binding.getRoot():null;
            if (null!=root){
                mBinding=binding;
                Clicker.setInterrupterTag(root,null);
                dialog.setContentView(root);
                Resources resources =null!=context?context.getResources():null;
                DisplayMetrics dm = null!=resources?resources.getDisplayMetrics():null;
                int width=null!=dm?dm.widthPixels:800;
                dialog.getWindow().setLayout((int)((width<=0?800:width)*0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
        return this;
    }

    public final Dialog setContentView(View view){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view&&null==view.getParent()){
            create();
            DialogLayoutBinding binding=mBinding;
            if (null!=binding){
                binding.setContentLayout(view);
            }
        }
        return this;
    }

    public final Dialog title(Object title){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setTitleText(title);
        }
        return this;
    }

    public final Dialog message(Object msgTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setMessageText(msgTextId);
        }
        return this;
    }

    public final Dialog left(Object sureTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setLeftText(sureTextId);
        }
        return this;
    }

    public final Dialog right(Object cancelTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setRightText(cancelTextId);
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

    public final boolean isShowing(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog&&dialog.isShowing();
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

    protected final String getText(Object object){
        if (null!=object){
            if (object instanceof String){
                return (String)object;
            }else if (object instanceof Integer&&!object.equals(Resources.ID_NULL)){
                Context context=getContext();
                return null!=context?context.getResources().getString((Integer)object):null;
            }
        }
        return null;
    }

    protected void onShow(){
        //Do nothing
    }

    public final boolean show(){
        return show(null,true);
    }

    public final boolean show(OnTapClick click){
        return show(click,null!=click&&(click instanceof Model||click instanceof Activity||
                click instanceof Service ||click instanceof BroadcastReceiver));
    }

    public final boolean show(OnTapClick click,boolean weak){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            DialogLayoutBinding binding=mBinding;
            View root=null!=binding?binding.getRoot():null;
            if (null!=root){
                Clicker.setInterrupterTag(root,click,weak);
            }
            dialog.show();
            onShow();
            return true;
        }
        return false;
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

    @Override
    public void onClick(View v) {
        //DO nothing
    }
}
