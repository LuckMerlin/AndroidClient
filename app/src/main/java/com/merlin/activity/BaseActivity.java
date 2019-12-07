package com.merlin.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.classes.Classes;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseModel> extends Activity {
    private V mBinding;
    private VM mViewModel;

    protected abstract int findContentViewId();

    private VM createViewModel(){
        Type type=getClass().getGenericSuperclass();
        if (null!=type&&type instanceof ParameterizedType){
            Type[] args=((ParameterizedType)type).getActualTypeArguments();
            if (null!=args&&args.length>0){
                Classes classes=new Classes();
                for (Type f:args){
                    if (null!=f&&f instanceof Class){
                        if (classes.isAssignableFrom((Class<?>) f,ViewDataBinding.class)){
                            Debug.D(getClass(),"找到  "+f);
                        }
                    }
                }
            }
        }
        return null;
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
        mBinding = DataBindingUtil.setContentView(this, findContentViewId());
        createViewModel();
//        mBinding.setVariable(BR.vm, mViewModel = createViewModel());
    }


}
