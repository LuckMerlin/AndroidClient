package com.merlin.activity;

import android.content.Intent;

import androidx.databinding.ViewDataBinding;

import com.merlin.bean.ClientMeta;
import com.merlin.model.BaseModel;
import com.merlin.protocol.Tag;

import java.io.Serializable;

public class NasActivity<V extends ViewDataBinding, VM extends BaseModel> extends  SocketActivity<V, VM> implements Tag {

    protected final ClientMeta getNasMetaFromIntent(Intent intent){
        Serializable serializable=null!=intent?intent.getSerializableExtra(TAG_META):null;
        ClientMeta meta=null!=serializable&&serializable instanceof ClientMeta ?(ClientMeta)serializable:null;
        return null==meta||null==meta.getAccount() || !meta.isDeviceType(TAG_NAS_DEVICE)?null:meta;
    }
}
