package com.merlin.dialog;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.merlin.client.R;
import com.merlin.client.databinding.DialogLayoutBinding;
import com.merlin.model.Model;
import com.merlin.view.Clicker;
import com.merlin.view.OnTapClick;

public class Dialog implements View.OnClickListener{
    private final android.app.Dialog mDialog;
    private DialogLayoutBinding mBinding;

    public Dialog(Context context){
        this(context,null,null);
    }

    public Dialog(ViewDataBinding contentBinding){
        this(null,contentBinding,null);
    }

    public Dialog(Context context, ViewDataBinding contentBinding,Integer windowType){
        this(context,contentBinding,windowType,null);
    }

    public Dialog(Context context, ViewDataBinding contentBinding,Integer windowType,int[] padding){
        View view=null==context&&null!=contentBinding?contentBinding.getRoot():null;
        android.app.Dialog dialog=mDialog=new android.app.Dialog(null!=view?view.getContext():context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setVolumeControlStream();
        Window window=dialog.getWindow();
        if (null!=windowType){
            window.setType(windowType);
        }
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
            onDismiss();
        });
        params.dimAmount = 0f;
        window.setAttributes(params);
        if (null!=contentBinding){
            setContentView(contentBinding,false,padding);
        }
    }

    public final Dialog setContentView(int layoutId,boolean recreate){
        Context context=getContext();
        if (null!=context){
            LayoutInflater inflater=LayoutInflater.from(context);
            ViewDataBinding binding=DataBindingUtil.inflate(inflater,layoutId,null,false);
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

    public final Dialog gravity(int gravity){
        android.app.Dialog dialog=mDialog;
        Window window=null!=dialog?dialog.getWindow():null;
        if (null!=dialog){
            window.setGravity(gravity);
        }
        return this;
    }

    public final Dialog create() {
        return create(null);
    }

    public final Dialog create(int[] padding){
        return create(null,null);
    }

    public final Dialog create(Integer w, Integer h){
        return create(w,h,null);
    }

    public final Dialog create(Integer w, Integer h,int[] padding){
        final android.app.Dialog dialog=mDialog;
        final Context context=null!=dialog?dialog.getContext():null;
        if (null!=dialog&&null!=context){
            DialogLayoutBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_layout,null,false);
            View root=null!=binding?binding.getRoot():null;
            if (null!=root){
                padding(root,null!=padding&&padding.length>3?padding:new int[]{dip2px(context,20),dip2px(context,20),
                        dip2px(context,10),dip2px(context,10)});
                mBinding=binding;
                Clicker.setInterrupterTag(root,null);
                dialog.setContentView(root,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Resources resources =null!=context?context.getResources():null;
                DisplayMetrics dm = null!=resources?resources.getDisplayMetrics():null;
                int width=null!=dm?dm.widthPixels:800;
                dialog.getWindow().setLayout(null!=w?w:(int)((width<=0?800:width)*0.75), null!=h?h:ViewGroup.LayoutParams.WRAP_CONTENT);
                ViewGroup.LayoutParams params=root.getLayoutParams();
                params=null!=params?params:new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                root.setLayoutParams(params);
            }
        }
        return this;
    }


    public final boolean isCreated(){
        return null!=mBinding;
    }

    public final Dialog padding(int[] padding){
        DialogLayoutBinding binding=mBinding;
        View root=null!=binding?binding.getRoot():null;
        padding(root,padding);
        return this;
    }

    private final Dialog padding(View view,int[] padding){
        if (null!=view&&padding.length>3){
            view.setPadding(padding[0],padding[1],padding[2],padding[3]);
        }
        return this;
    }

    public final Dialog cleanBackground(){
        DialogLayoutBinding binding=mBinding;
        View root=null!=binding?binding.getRoot():null;
        if (null!=root){
            root.setBackgroundColor(Color.TRANSPARENT);
        }
        return this;
    }

    public final Dialog setBackground(Object background){
//        DialogLayoutBinding binding=mBinding;
//        View root=null!=binding?binding.getRoot():null;
//        if (null!=root){
//            applyBackground(background);
//        }
//
//
//        if (null!=background&&background instanceof String&&((String)background).length()>0){
//            DialogLayoutBinding binding=mBinding;
//            View root=null!=binding?binding.getRoot():null;
//            if (null!=root){
//                if (null!=background){
//                    Glide.with(root).load(((String)background)).into(new CustomViewTarget<View,Drawable>(root) {
//                        @Override
//                        protected void onResourceCleared(@Nullable Drawable placeholder) {
//
//                        }
//
//                        @Override
//                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//                        }
//
//                        @Override
//                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                            applyBackground(resource);
//                        }
//                    });
//                }
//            }
//        }else {
//            applyBackground(background);
//        }
        applyBackground(background);
        return this;
    }

    private boolean applyBackground(Object background){
        DialogLayoutBinding binding=mBinding;
        View root=null!=binding?binding.getRoot():null;
        if (null!=root) {
            Drawable drawable = null;
            if (null != background) {
                if (background instanceof Drawable) {
                    drawable = (Drawable) background;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                android:background="@drawable/radius_corner_dialog"
                root.setBackground(drawable);
            } else {
                root.setBackgroundDrawable(drawable);
            }
            return true;
        }
        return false;
    }

    public final Dialog setContentView(View view,boolean recreate){
        return setContentView(view,recreate,null);
    }

    public final Dialog setContentView(View view,boolean recreate,int[] padding){
        android.app.Dialog dialog=mDialog;
        if (null!=dialog&&null!=view&&null==view.getParent()){
            if (!isCreated()||recreate) {
                create(padding);
            }
            DialogLayoutBinding binding=mBinding;
            if (null!=binding){
                binding.setContentLayout(view);
            }
        }
        return this;
    }

    public final Dialog setLayoutParams(ViewGroup.LayoutParams params){
        DialogLayoutBinding binding=mBinding;
        View root=null!=binding?binding.getRoot():null;
        if (null!=binding){
            root.setLayoutParams(params);
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

    public final Dialog left(Object leftTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setLeftText(leftTextId);
        }
        return this;
    }

    public final Dialog center(Object centerTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setCenterText(centerTextId);
        }
        return this;
    }

    public final Dialog right(Object rightTextId){
        DialogLayoutBinding binding=mBinding;
        if (null!=binding){
            binding.setRightText(rightTextId);
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
        return show(click,null!=click&&(click instanceof Model ||click instanceof Activity||
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
        }
        return this;
    }

    public final Context getContext(){
        android.app.Dialog dialog=mDialog;
        return null!=dialog?dialog.getContext():null;
    }

    public final View getRootView() {
        ViewDataBinding binding=mBinding;
        return null!=binding?binding.getRoot():null;
    }

    public final DialogLayoutBinding getBinding() {
        return mBinding;
    }

    @Override
    public void onClick(View v) {
        //D0 nothing
    }

    private int dip2px(Context context, float dpValue) {
        if (dpValue>0){
            Resources resources=null!=context?context.getResources():null;
            DisplayMetrics metrics=null!=resources?resources.getDisplayMetrics():null;
            return null!=metrics?(int) (dpValue * metrics.density + 0.5f):null;
        }
        return 0;
    }

}
