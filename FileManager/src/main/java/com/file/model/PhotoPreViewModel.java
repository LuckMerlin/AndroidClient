package com.file.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.ObservableField;

import com.merlin.api.Label;
import com.merlin.model.Model;

import java.util.ArrayList;
import java.util.List;

public class PhotoPreViewModel extends BaseModel implements Model.OnActivityIntentChange {
    private final ObservableField<Object> mShowing=new ObservableField<>();
    private List mImages;

    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        Bundle extra=null!=intent?intent.getExtras():null;
        Object object=null!=extra?extra.get(Label.LABEL_DATA):null;
        if (null!=object&&!(object instanceof List)){
            List list=new ArrayList(1);
            list.add(object);
            object=list;
        }
        if (null==object||!(object instanceof List)||((List)object).size()<=0){
            finishActivity("While none photo need preview.");
            return;
        }
        mImages=(List)object;
        apply(null,"While intent changed");
    }

    private boolean apply(Object image,String debug){
        List images=mImages;
        if (null!=images&&images.size()>0){
            Object showing=null==image?mShowing:null;
            if (null!=showing&&images.contains(showing)){
                return false;
            }
           mShowing.set(images.get(0));
            return true;
        }
        return false;
    }


    public ObservableField<Object> getImage() {
        return  mShowing;
    }
}
