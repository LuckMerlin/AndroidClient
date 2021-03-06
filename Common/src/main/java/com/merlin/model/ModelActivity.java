package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.merlin.debug.Debug;
import com.merlin.lib.R;

public class ModelActivity<T extends Model>extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.D(getClass(),"Activity onCreate."+this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        notifyIntentChanged(intent);
    }

    protected final void setModelContentView(int layoutId){
        DataBindingUtil.setContentView(this,  layoutId);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Model model=getModel();
        if (null!=model&&model instanceof Model.OnActivityResume) {
            ((Model.OnActivityResume)model).onActivityResume(this,getIntent());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        notifyIntentChanged(getIntent());
    }

    @Override
    public void onBackPressed() {
        Object viewModel=getModel();
        if (null==viewModel||!(viewModel instanceof Model.OnActivityBackPress)
                ||!(((Model.OnActivityBackPress)viewModel).onActivityBackPressed(this))){
            super.onBackPressed();
        }
    }

    public void onModelBind(Model model){
        //DO nothing
    }

    public final  T getModel(Class<T> cls){
        Model model=getModel();
        return null!=model&&(null==cls||model.getClass().isAssignableFrom(cls))?(T)model:null;
    }

    public final  Model getModel(){
        View view=getWindow().getDecorView();
        view=null!=view?view.findViewById(android.R.id.content):null;
        if (null!=view&&view instanceof ViewGroup){
            ViewGroup vg=(ViewGroup)view;
            int count=vg.getChildCount();
            View child;
            Object object;
            for (int i=0;i<count;i++){
                if (null!=(child=vg.getChildAt(i))&&null!=(object=child.getTag(R.id.modelBind))&&
                        object instanceof Model){
                    return ((Model)object);
                }
            }
        }
        return null;
    }

    protected void onIntentChanged(Intent intent){
        //DO nothing
    }

    private void notifyIntentChanged(Intent intent){
        onIntentChanged(intent);
        Object vm=null!=intent?getModel():null;
        if (null!=vm&&vm instanceof Model.OnActivityIntentChange){
            ((Model.OnActivityIntentChange)vm).onActivityIntentChanged(this,intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Object vm=getModel();
        if (null!=vm&&vm instanceof Model.OnActivityResult){
            ((Model.OnActivityResult)vm).onActivityResult(this,requestCode,resultCode,data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"Activity onDestroy."+this);
    }
}
