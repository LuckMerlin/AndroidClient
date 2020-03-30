package com.merlin.global;
import android.app.Application;
import android.util.SparseArray;
import com.merlin.client.Client;
import com.merlin.client.OnConnectChange;

import java.util.ArrayList;
import java.util.List;

public final class Clients {
    private final List<OnConnectChange> mChanges=new ArrayList<>();
    private final SparseArray<Client> mClients=new SparseArray<>();

    public List<Client> getCloudClients(){

        return null;
    }

    public boolean add(OnConnectChange change,String debug){
        List<OnConnectChange> changes=null!=change?mChanges:null;
        return null!=changes&&!changes.contains(change)&&changes.add(change);
    }

    public boolean remove(OnConnectChange change,String debug){
        List<OnConnectChange> changes=null!=change?mChanges:null;
        return null!=changes&&changes.remove(change);
    }

    boolean init(Application application,String debug){
        if (null!=application){

        }
        return false;
    }

}
