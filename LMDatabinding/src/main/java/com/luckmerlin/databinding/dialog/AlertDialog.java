package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableField;

public class AlertDialog extends Dialog {
    private final ObservableField<String> mTitle=new ObservableField<>();
    private final ObservableField<String> mLeft=new ObservableField<>();
    private final ObservableField<String> mCenter=new ObservableField<>();
    private final ObservableField<String> mRight=new ObservableField<>();
    private final ObservableField<String> mMessage=new ObservableField<>();

    public AlertDialog(Context context){
        this(context,null);
    }

    public AlertDialog(Context context, Integer windowType){
        this(context,windowType,null);
    }

    public AlertDialog(Context context,Integer windowType, Drawable background){
        this(null!=context?new android.app.Dialog(context):null,windowType,background);
    }

    public AlertDialog(android.app.Dialog dialog,Integer windowType, Drawable background){
        super(dialog,windowType,background);
    }



}
