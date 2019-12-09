package com.merlin.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.classes.Classes;
import com.merlin.client.BR;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseModel> extends Activity {
    private V mBinding;
    private VM mViewModel;

    private void createViewModel(){
        Type type=getClass().getGenericSuperclass();
        if (null!=type&&type instanceof ParameterizedType){
            Type[] args=((ParameterizedType)type).getActualTypeArguments();
            if (null!=args&&args.length>0){
                Classes classes=new Classes();
                VM vm=null;
                Integer bindingId=null;
                for (Type f:args){
                    if (null!=f&&f instanceof Class){
                        if (null==vm&&classes.isAssignableFrom((Class<?>) f,BaseModel.class)){
                            Class cls=(Class)f;
                            try {
                                Constructor constructor=cls.getConstructor(Context.class);
                                if (null!=constructor){
                                    constructor.setAccessible(true);
                                    vm=(VM)constructor.newInstance(this);
                                }
                            } catch (Exception e) {
                                try {
                                    Constructor constructor= cls.getConstructor();
                                    if (null!=constructor) {
                                        constructor.setAccessible(true);
                                        vm=(VM)constructor.newInstance();
                                    }
                                } catch (Exception e1) {
                                    Debug.E(getClass(),""+cls,e1);
                                }
                            }
                        }else if (null==bindingId&&classes.isAssignableFrom((Class<?>) f,ViewDataBinding.class)){
                            Field[] fields=R.layout.class.getDeclaredFields();
                            if (null!=fields&&fields.length>0){
                                String target=((Class<?>) f).getSimpleName().toLowerCase(Locale.CHINESE);
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
                        if (null!=bindingId&&null!=vm){
                            mViewModel=vm;
                            mBinding = DataBindingUtil.setContentView(this, bindingId);
                            mBinding.setVariable(BR.vm, vm);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
//      tintManager.setNavigationBarTintResource(R.drawable.top_back);
        //tintManager.setTintColor(Color.parseColor("#ff0000"));
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        createViewModel();
    }

    public final V getBinding() {
        return mBinding;
    }

    public final VM getViewModel() {
        return mViewModel;
    }
}
