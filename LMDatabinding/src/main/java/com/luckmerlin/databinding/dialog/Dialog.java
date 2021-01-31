package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.ModelClassFinder;

public class Dialog implements PublishMethods{
    private final android.app.Dialog mDialog;
    private Object mDispatchHolder;

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
            dialog.setOnShowListener((DialogInterface dlg)-> {
                onDialogShow();
                Model model=getContentModel();
                if (null!=model){
                    model.addDispatchHolder(Dialog.this);
                    Object dispatch=mDispatchHolder;
                    if (null!=dispatch){
                        model.addDispatchHolder(dispatch);
                    }
                }
            });
            dialog.setOnDismissListener((DialogInterface dlg)-> {
                onDialogDismiss();
                Object innerDispatchHolder=mDispatchHolder;
                mDispatchHolder=null;
                Model model=getContentModel();
                if (null!=model){
                    model.removeDispatchHolder(Dialog.this);
                    if (null!=innerDispatchHolder){
                        model.removeDispatchHolder(innerDispatchHolder);
                    }
                }
            });
            Window window=dialog.getWindow();
            if (null!=window){
                window.setDimAmount(0);
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

    public final Dialog setEnterTransition(Transition transition){
        Window window=getWindow();
        if (null!=window){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setEnterTransition(transition);
            }
        }
        return this;
    }

    public final Dialog setExitTransition(Transition transition){
        Window window=getWindow();
        if (null!=window){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setExitTransition(transition);
            }
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
        return setContentView(model,null,null);
    }

    public final Dialog setContentView(Model model,ViewGroup.LayoutParams params){
        return setContentView(model,null,params);
    }

    public final Dialog setContentView(Model model, int[] padding){
        return setContentView(model,padding,null);
    }

    public final Model getContentModel(){
        View root=getContentRoot();
        MatchBinding matchBinding=null!=root?new ModelClassFinder().findModel(root,null):null;
        Object currentObject=null!=matchBinding?matchBinding.getCurrent():null;
        return null!=currentObject&&currentObject instanceof Model?((Model)currentObject):null;
    }

    public final View getContentRoot(){
         Window window=getWindow();
         View decorView=null!=window?window.getDecorView():null;
         decorView= null!=decorView?decorView.findViewById(android.R.id.content):null;
         if (null!=decorView&&decorView instanceof ViewGroup){
             ViewGroup vg=(ViewGroup)decorView;
             int count=vg.getChildCount();
             View child=null;
             for (int i = 0; i < count; i++) {
                 if (null!=(child=vg.getChildAt(i))&&child.getVisibility()==View.VISIBLE){
                    return child;
                 }
             }
         }
         return null;
    }

    public final Dialog setContentView(Model model, int[] padding, ViewGroup.LayoutParams params){
        Context context=null!=model&&!model.isRootAttached()?getContext():null;
        MatchBinding matchBinding=null!=context?new ModelBinder().bindModelForObject(context,model,"While set dialog content view."):null;
        View dialogView=null!=matchBinding?matchBinding.getRoot():null;
        if (null!=dialogView){
            setContentView(dialogView,padding,params);
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

    protected void onRootChanged(View current,View last){
        //Do nothing
    }

    public final Dialog setContentView(View view, int[] padding, ViewGroup.LayoutParams params){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view&&null==view.getParent()){
            if (null!=padding&&padding.length==4){
                view.setPadding(padding[0],padding[1],padding[2],padding[3]);
            }
            if (view.getParent()==null){
                View current=getRoot();
                dialog.setContentView(view,null!=params?params:new ViewGroup.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                onRootChanged(view,current);
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
        return show(null);
    }

    protected void onDialogShow(){
        //Do nothing
    }

    protected void onDialogDismiss(){
        //Do nothing
    }

    public final boolean show(Object dispatchHolder){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&!dialog.isShowing()){
            Object current=mDispatchHolder;
            Model model=null!=current?getContentModel():null;
            if (null!=model){
                model.removeDispatchHolder(current);
            }
            mDispatchHolder=dispatchHolder;
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

    public final Object getDispatchHolder() {
        return mDispatchHolder;
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

    public final Dialog setSize(int width,int height){
        Window window=getWindow();
        if (null!=window){
            window.setLayout(width,height);
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
