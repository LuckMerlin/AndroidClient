package com.merlin.global;

import android.widget.Toast;

public abstract class Service extends android.app.Service {

//    protected final CharSequence getText(int resId){
//        return getResources().getText(resId);
//    }

    protected final boolean toast(int resId,Object ...args){
        CharSequence charSequence=getText(resId);
        return null!=charSequence&&toast(charSequence,args);
    }

    protected final boolean toast(CharSequence text,Object ...args){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
        return true;
    }
}
