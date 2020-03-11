package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.merlin.api.Label;
import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PhotoPreViewModel extends Model implements Model.OnActivityIntentChange {
    private PhotoView mPhotoView;
    private PhotoViewAttacher mAttacher;
    private Collection mCollection;
    private Object mShowing;

    public PhotoPreViewModel(Context context){
        mPhotoView = new PhotoView(context);
        mAttacher=new PhotoViewAttacher(mPhotoView);
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        PhotoView photoView=mPhotoView;
        if (null!=root&&root instanceof ViewGroup) {
            if (null!=photoView&&null==photoView.getParent()) {
                ((ViewGroup) root).addView(photoView, new ViewGroup.LayoutParams(ViewGroup.
                        LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        Bundle bundle=null!=intent?intent.getExtras():null;
        Object extra=null!=bundle?bundle.get(Label.LABEL_DATA):null;
        if (null!=extra&&extra instanceof Uri){
            List list=new ArrayList<>(1);
            list.add(extra);
            extra=list;
        }
        if (null!=extra&&extra instanceof Collection){
            mCollection=(Collection)extra;
            showNext("After activity intent changed.");
        }
    }

    private boolean showNext(String debug){
        Collection collection=mCollection;
        PhotoView photoView=mPhotoView;
        PhotoViewAttacher attacher=mAttacher;
        if (null!=photoView&&null!=attacher&&null!=collection&&collection.size()>0){
            Object showing=mShowing;
            Object next=null;
            for (Object child:collection) {
                if (null!=child){
                    if (next!=null){
                        next=child;
                        break;
                    }else if(child.equals(showing)){
                        next=true;
                    }
                }
            }
            next=null!=next&&!(next instanceof Boolean)?next:collection.iterator().next();
            if (null!=next){
                if (next instanceof String){
                    File file=new File((String)next);
                    if (file.exists()){
                        photoView.setImageURI(Uri.fromFile(file));
                        attacher.update();
                    }
                }else if (next instanceof Integer){
                    photoView.setImageResource((Integer)next);
                    attacher.update();
                }else if (next instanceof Uri){
                    photoView.setImageURI((Uri)next);
                    attacher.update();
                }
            }
        }
        return false;
    }
}
