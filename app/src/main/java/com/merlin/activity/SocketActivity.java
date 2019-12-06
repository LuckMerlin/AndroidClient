package com.merlin.activity;

import android.app.Application;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.Client;
import com.merlin.model.BaseModel;

import java.lang.ref.WeakReference;


public class SocketActivity <V extends ViewDataBinding, VM extends BaseModel> extends BaseActivity<V,VM>{
    private WeakReference<Client> mReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application application=getApplication();
        if (null!=application&&application instanceof com.merlin.global.Application) {
            Client client=((com.merlin.global.Application) application).getClient();
            mReference=null!=client?new WeakReference<Client>(client):null;
        }
        if (null==mReference){
            throw new Exception("Activity service client NONE.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
