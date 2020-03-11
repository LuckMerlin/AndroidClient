package com.merlin.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class FileReceiveActivity extends  ModelActivity<FileBrowserModel> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onIntentChanged(Intent intent) {
        String action=null!=intent?intent.getAction():null;
//        android.intent.extra.STREAM content://media/external/file/195790
        if (null!=action){
            if (action.equals(Intent.ACTION_SEND)||action.equals(Intent.ACTION_SEND_MULTIPLE)){
                Bundle bundle=intent.getExtras();
                Object data=null!=bundle?bundle.get(Intent.EXTRA_STREAM):null;
                if (null!=data&&data instanceof Uri){
                    Uri uri=(Uri)data;
                    ((ArrayList)(data=new ArrayList<>(1))).add(uri);
                }
                if (null!=data&&data instanceof Collection){
                    for (Object child:(Collection)data){
                        Field[] fields=null!=child?child.getClass().getDeclaredFields():null;
                        Debug.D(getClass(),"AchildAA "+child.getClass().getName());
                        if (null!=fields&&fields.length>0){
                           for (Field field:fields){
                               Debug.D(getClass(),"AAA "+field);
                           }
                        }
                    }
                }
            }
        }
        finish();
    }

}
