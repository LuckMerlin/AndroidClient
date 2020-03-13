//package com.merlin.activity;
//
//import android.content.Intent;
//import android.os.Parcelable;
//
//import androidx.databinding.ViewDataBinding;
//
//import com.merlin.bean.ClientMeta;
//import com.merlin.protocol.Tag;
//
//public class NasActivity<V extends ViewDataBinding, VM extends BaseModel> extends  SocketActivity<V, VM> implements Tag {
//
//    protected final ClientMeta getNasMetaFromIntent(Intent intent){
//        Parcelable parcelable=null!=intent?intent.getParcelableExtra(TAG_META):null;
//        ClientMeta meta=null!=parcelable&&parcelable instanceof ClientMeta ?(ClientMeta)parcelable:null;
//        return null==meta||null==meta.getAccount() || !meta.isDeviceType(TAG_NAS_DEVICE)?null:meta;
//    }
//}
