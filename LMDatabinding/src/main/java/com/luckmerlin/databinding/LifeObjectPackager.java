package com.luckmerlin.databinding;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;

import java.lang.ref.WeakReference;

public final class LifeObjectPackager {

    public Object pack(Object object){
        if (null!=object) {
            if (object instanceof Activity || object instanceof Service || object instanceof BroadcastReceiver || object instanceof ContentProvider) {
                return new WeakReference<>(object);
            }
        }
        return object;
    }

    public Object get(Object packed){
        return null!=packed&&packed instanceof WeakReference?((WeakReference)packed).get():packed;
    }
}
