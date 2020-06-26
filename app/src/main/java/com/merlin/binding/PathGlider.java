package com.merlin.binding;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.merlin.api.Label;
import com.merlin.bean.IPath;
import com.merlin.util.Encoder;
import com.merlin.view.Clicker;
import com.merlin.view.Res;

import java.io.File;

public class PathGlider {

    public final boolean set(View view,Drawable image,boolean background){
        if (null!=view){
            if (background){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(image);
                }else{
                    view.setBackgroundDrawable(image);
                }
            }else if (view instanceof ImageView){
                ((ImageView)view).setImageDrawable(image);
            }
            return true;
        }
        return false;
    }

    public final boolean glide(View view, Object path, boolean background){
        if (null!=path&&null!=view){
            set(view,null,background);
            if (path instanceof Integer){
                if (background){


                    view.setBackgroundResource((Integer)path);
                }else if (view instanceof ImageView){
                    Clicker.putRes(view, new Res((Integer)path,null));
                    ((ImageView)view).setImageResource((Integer)path);
                }
                return true;
            }
            if (path instanceof Drawable){
                set(view,(Drawable)path,background);
            }else if (path instanceof String){
                path= IPath.build(path,null);
            }
            Context context=view.getContext();
            if (null!=path&&path instanceof IPath &&null!=context){
                IPath imagePath=(IPath)path;
                String nasPath=imagePath.getPath(null);
                String host=imagePath.getHostName();
                RequestBuilder<Drawable> builder=null;
                if (null!=nasPath&&nasPath.length()>0){
                    if (null!=host&&host.length()>0&&!imagePath.isLocal()){
                        GlideUrl glideUrl = new GlideUrl(host, new LazyHeaders.Builder().addHeader(Label
                                .LABEL_PATH, new Encoder().encode(nasPath,null,"utf-8")).build());
                        builder= Glide.with(context).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE);
                    }else{
                        builder=Glide.with(context).load(new File(nasPath));
                    }
                }
                if (null!=builder){
                    RoundedCorners roundedCorners = new RoundedCorners(1);
                    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(view.getWidth(),
                            view.getHeight());
                    CustomTarget<Drawable> simpleTarget = new CustomTarget<Drawable>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            set(view,resource,background);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    };

                    builder.centerCrop().apply(options).thumbnail(1f)
//                                .transform(new BlurMaskFilter)
//                                .bitmapTransform(new BlurTransformation(context, 5),//模糊转换
//                                new TopCropTransformation(context))
                                .into(simpleTarget);
                }
            }
            return false;
        }
        return false;
    }
}
