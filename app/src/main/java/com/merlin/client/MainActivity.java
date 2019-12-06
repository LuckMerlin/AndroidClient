package com.merlin.client;

import android.view.View;
import android.widget.Toast;

import com.merlin.activity.BaseActivity;
import com.merlin.model.BaseModel;
import com.merlin.model.LoginModel;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int findContentViewId() {
        return R.layout.activity_main;
    }

    public void ddd(View view){
        System.exit(1);
    }

    @Override
    protected BaseModel createViewModel() {
              return new LoginModel();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(),"dianjie "+v,Toast.LENGTH_LONG).show();
    }
}
