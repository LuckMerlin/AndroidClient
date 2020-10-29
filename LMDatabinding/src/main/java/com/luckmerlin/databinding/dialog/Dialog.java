package com.luckmerlin.databinding.dialog;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.touch.OnViewClick;

import java.lang.ref.WeakReference;

public class Dialog implements PublishMethods{
    private final android.app.Dialog mDialog;

    public Dialog(Context context){
        this(context,null);
    }

    public Dialog(Context context, Integer windowType){
        this(context,windowType,null);
    }

    public Dialog(Context context,Integer windowType, Drawable background){
        this(null!=context?new android.app.Dialog(context):null,windowType,background);
    }

    public Dialog(android.app.Dialog dialog,Integer windowType, Drawable background){
        mDialog=dialog;
        if (null!=dialog){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window=dialog.getWindow();
            if (null!=window){
                if (null!=windowType){
                    window.setType(windowType);
                }
                window.setBackgroundDrawable(null!=background?background:new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public final Window getWindow(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getWindow():null;
    }

    public final Dialog setSoftInputMode(int mode){
        Window window=getWindow();
        if (null!=window){
            window.setSoftInputMode(mode);
        }
        return this;
    }

    public final Dialog setDimAmount(float dimAmount){
        Window window=getWindow();
        if (null!=window){
            window.setDimAmount(dimAmount);
        }
        return this;
    }

    public final Dialog setContentView(int layoutId){
        Context context=getContext();
        if (null!=context){
            LayoutInflater inflater=LayoutInflater.from(context);
            ViewDataBinding binding= DataBindingUtil.inflate(inflater,layoutId,null,false);
            if (null!=binding){
                return setContentView(binding);
            }
            return setContentView(inflater.inflate(layoutId,null,false));
        }
        return this;
    }

    public final Dialog setContentView(Model model){
        Context context=null!=model&&!model.isRootAttached()?getContext():null;
        MatchBinding matchBinding=null!=context?new ModelBinder().bindModelForObject(context,model,"While set dialog content view."):null;
        View dialogView=null!=matchBinding?matchBinding.getRoot():null;
        Debug.D("BBBBBBBBBBBbb "+dialogView);
        if (null!=dialogView){
            setContentView(dialogView);
        }
        return this;
    }


    public final Dialog setContentView(ViewDataBinding binding){
        return setContentView(binding,null);
    }

    public final Dialog setContentView(ViewDataBinding binding,int[] padding){
        View view=null!=binding?binding.getRoot():null;
        return null!=view?setContentView(view,padding,null):this;
    }

    public final Dialog setContentView(View view){
        return setContentView(view,null,null);
    }

    public final LayoutInflater inflater(){
        Context context=getContext();
        return null!=context?LayoutInflater.from(context):null;
    }

    public final Dialog setContentView(View view, int[] padding, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view&&null==view.getParent()){
            if (null!=padding&&padding.length==4){
                view.setPadding(padding[0],padding[1],padding[2],padding[3]);
            }
            if (view.getParent()==null){
                dialog.setContentView(view,null!=params?params:new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        return this;
    }

    public final View getWindowRoot() {
         Window window=getWindow();
         return null!=window?window.getDecorView():null;
     }

    public final View getRoot(){
        android.app.Dialog dialog=mDialog;
        Window window=null!=dialog?dialog.getWindow():null;
        return null!=window?window.getDecorView():null;
     }

    public final boolean show(){
        return show(null,true);
    }

    public final boolean show(OnViewClick click){
        return show(click,null!=click&&(click instanceof ContentProvider ||click instanceof Activity ||
                click instanceof Service ||click instanceof BroadcastReceiver));
    }

    protected void onDialogShow(){
        //Do nothing
    }

    protected void onDialogDismiss(){
        //Do nothing
    }

    public final boolean show(OnViewClick click, boolean weak){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            onDialogShow();
            dialog.show();
            return true;
        }
        return false;
    }

    public final Dialog dismiss(){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&dialog.isShowing()){
            dialog.dismiss();
            onDialogDismiss();
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

    public final WindowManager.LayoutParams getAttributes(){
        Window window=getWindow();
       return null!=window?window.getAttributes():null;
    }

    public final Dialog setAttributes(WindowManager.LayoutParams params){
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

    public final Dialog setGravity(int gravity){
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

}
