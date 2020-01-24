package com.merlin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.classes.Classes;
import com.merlin.client.BR;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;
import com.merlin.util.StatusBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseModel> extends Activity {
    private V mBinding;
    private VM mViewModel;

    private Class findFieldClass(Class<?> cls,Type[] args,Classes classes){
        if (null!=cls&&null!=args&&null!=classes) {
            for (Type f : args) {
                if (classes.isAssignableFrom((Class<?>) f,cls)){
                    return (Class)f;
                }
            }
        }
        return null;
    }

    private Object createInstance(Class cls){
        if (null!=cls) {
            try {
                Constructor constructor = cls.getConstructor(Context.class);
                if (null != constructor) {
                    constructor.setAccessible(true);
                    return constructor.newInstance(this);
                }
            } catch (NoSuchMethodException | IllegalArgumentException |
                    InstantiationException | InvocationTargetException | IllegalAccessException e) {
                try {
                    Constructor constructor = cls.getConstructor();
                    if (null != constructor) {
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    }
                } catch (Exception e1) {
                    Debug.E(getClass(), "" + cls, e1);
                }
            }
        }
        return null;
    }

    private void createViewModel(){
        Type type=getClass().getGenericSuperclass();
        Type[] args=null!=type&&type instanceof ParameterizedType?((ParameterizedType)type).getActualTypeArguments():null;
        if (null==args||args.length<=0){
            return;
        }
        Classes classes=new Classes();
        Class cls=findFieldClass(ViewDataBinding.class,args,classes);
        Integer bindingId=null;
        if (null!=cls){
            Field[] fields=R.layout.class.getDeclaredFields();
            if (null!=fields&&fields.length>0){
                String target=((Class<?>) cls).getSimpleName().toLowerCase(Locale.CHINESE);
                for (Field field:fields){
                    if (null!=field){
                        field.setAccessible(true);
                        String name=field.getName();
                        name=null!=name?name.replaceAll("_",""):null;
                        if (null!=name&&(name+"binding").equals(target)){
                            try {
                                bindingId=field.getInt(null);
                                break;
                            } catch (IllegalAccessException e) {
                                //Do nothing
                            }
                        }
                    }
                }
            }
        }
        ViewDataBinding binding=mBinding =(null!=bindingId?DataBindingUtil.setContentView(this, bindingId):null);
        cls=findFieldClass(BaseModel.class,args,classes);
        final VM vm=mViewModel=null!=cls?(VM)createInstance(cls):null;
        if (null!=binding && null!=vm) {
            try {
                View root = binding.getRoot();
                if (null!=root){
                    Method method = BaseModel.class.getDeclaredMethod("setRootView",View.class);
                    if (null != method) {
                        method.setAccessible(true);
                        method.invoke(vm,root);
                    }
                }
            } catch (Exception e) {
                //Do nothing
            }
            binding.setVariable(BR.vm, vm);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                   | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                   | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        createViewModel();
        onIntentChanged(getIntent());
    }


    @Override
    public void onBackPressed() {
        VM viewModel=mViewModel;
        if (null==viewModel||!(viewModel instanceof OnBackPressed)
        ||!(((OnBackPressed)viewModel).onBackPressed())){
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntentChanged(intent);
    }

    private void onIntentChanged(Intent intent){
        VM vm=null!=intent?mViewModel:null;
        if (null!=vm&&vm instanceof BaseModel.OnIntentChanged){
            ((BaseModel.OnIntentChanged)vm).onIntentChange(intent);
        }
    }

    protected final V getBinding() {
        return mBinding;
    }

    public final VM getViewModel() {
        return mViewModel;
    }

    protected final void toast(Object value){
        value=null!=value?value instanceof String?value:value instanceof Integer
                ?getString((Integer)value):null:null;
        if (null!=value&&value instanceof String){
            Toast.makeText(this,(String)value,Toast.LENGTH_LONG).show();
        }
    }

//    protected final <T>T loadDataFromIntent(Intent intent,Class<T> cls){
//        intent=null!=intent?intent:getIntent();
//        Parcelable parcelable=null!=intent?intent.getParcelableExtra(BaseModel.LABEL_ACTIVITY_DATA):null;
//        if (null!=parcelable&&parcelable.getClass().isAssignableFrom(cls)){
//            return (T)parcelable;
//        }
//        return null;
//    }
}
