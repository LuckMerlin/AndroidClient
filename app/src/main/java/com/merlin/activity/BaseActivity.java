package com.merlin.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.BR;
import com.merlin.model.BaseModel;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseModel> extends Activity {
    private V mBinding;
    private VM mViewModel;

    protected abstract int findContentViewId();

    protected VM createViewModel(){
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
        mBinding.setVariable(BR.vm, mViewModel = createViewModel());
    }


}
