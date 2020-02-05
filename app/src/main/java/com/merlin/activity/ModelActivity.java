package com.merlin.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import com.merlin.model.BaseModel;
import com.merlin.model.Model;

public class ModelActivity <T extends Model>extends Activity {

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
        onIntentChanged(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntentChanged(intent);
    }

    @Override
    public void onBackPressed() {
        Object viewModel=getModel();
        if (null==viewModel||!(viewModel instanceof OnBackPressed)
                ||!(((OnBackPressed)viewModel).onBackPressed())){
            super.onBackPressed();
        }
    }

    public final  T getModel(Class<T> cls){
        Model model=getModel();
        return null!=model&&(null==cls||model.getClass().isAssignableFrom(cls))?(T)model:null;
    }

    public final  Model getModel(){

        return null;
    }

    private void onIntentChanged(Intent intent){
        Object vm=null!=intent?getModel():null;
        if (null!=vm&&vm instanceof BaseModel.OnIntentChanged){
            ((BaseModel.OnIntentChanged)vm).onIntentChange(intent);
        }
    }

}
