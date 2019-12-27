package com.merlin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.merlin.bean.Meta;
import com.merlin.model.BaseModel;
import com.merlin.protocol.Tag;

import java.io.Serializable;

public class NasActivity<V extends ViewDataBinding, VM extends BaseModel> extends  SocketActivity<V, VM> implements Tag {

    protected final Meta getNasMetaFromIntent(Intent intent){
        Serializable serializable=null!=intent?intent.getSerializableExtra(TAG_META):null;
        Meta meta=null!=serializable&&serializable instanceof Meta?(Meta)serializable:null;
        return null==meta||null==meta.getAccount() || !meta.isDeviceType(TAG_NAS_DEVICE)?meta:null;
    }
}
