package com.merlin.model;

import android.view.View;
import android.widget.Toast;

public class LoginModel extends BaseModel {

    @Override
    public void onViewClick(View v, int id) {
        Toast.makeText(v.getContext(),"dianjie "+v,Toast.LENGTH_LONG).show();
    }
}
