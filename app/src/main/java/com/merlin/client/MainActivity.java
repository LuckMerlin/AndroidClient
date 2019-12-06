package com.merlin.client;

import android.os.Bundle;
import android.view.View;

import com.merlin.activity.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ddd(View view){
        System.exit(1);
    }


}
