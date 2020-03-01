package com.merlin.transport;

import android.content.Context;
import android.widget.Toast;

public abstract class Transporter {
    protected abstract Context getContext();

    protected final boolean toast(int textId){
        Context context=getContext();
        String text=null!=context?context.getString(textId):null;
        return null!=text&&null!=context&&toast(text);
    }

    protected final boolean toast(String text){
        Context context=getContext();
        if (null!=context&&null!=text){
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
