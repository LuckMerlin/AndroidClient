package com.merlin.model;

import android.view.View;

public class BaseModel implements View.OnClickListener {

    public void onViewClick(View v,int id){
        //Do nothing
    }

    @Override
    public final void onClick(View v) {
        if (null!=v) {
            onViewClick(v,v.getId());
        }
    }
}
