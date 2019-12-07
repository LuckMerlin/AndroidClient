package com.merlin.activity;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.merlin.classes.Classes;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


public class SocketActivity <V extends ViewDataBinding, VM extends BaseModel> extends BaseActivity<V,VM>{
    private WeakReference<Client> mReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application application=getApplication();
        if (null!=application&&application instanceof com.merlin.global.Application) {
            Field[] fields=com.merlin.global.Application.class.getDeclaredFields();
            if (null!=fields&&fields.length>0){
                Classes classes=new Classes();
                for (Field f:fields){
                    Class cls=null!=f?f.getType():null;
                    if (null!=cls&&classes.isAssignableFrom(cls, Client.class)){
                        f.setAccessible(true);
                        try {
                           Object object= f.get(application);
                           if (null!=object&&object instanceof Client){
                               mReference=new WeakReference<>((Client)object);
                               break;
                           }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (null==mReference){
            Debug.E(getClass(),"Can't create activity client ");
        }
    }

    private Client getClient(){
        WeakReference<Client> reference=mReference;
        return null!=reference?reference.get():null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeakReference<Client> reference= mReference;
        mReference=null;
        if (null!=reference){
            reference.clear();
        }
    }
}
