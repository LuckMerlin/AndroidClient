package com.merlin.browser.binding;

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
import com.merlin.api.Res;
import com.merlin.bean.Path;
import com.merlin.click.Clicker;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

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
//                path= LocalPath.build(path,null);
            }
            Context context=view.getContext();
            if (null!=path&&path instanceof Path &&null!=context){
                Path imagePath=(Path)path;
                String nasPath=imagePath.getPath();
                String host=imagePath.getHost();
                RequestBuilder<Drawable> builder=null;
                if (null!=nasPath&&nasPath.length()>0){
                    if (null!=host&&host.length()>0&&!imagePath.isLocal()){
                        GlideUrl glideUrl = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                            try {
//                                new LazyHeaders.Builder().addHeader(Label.LABEL_PATH, Base64.getEncoder()
//                                        .encode(nasPath.getBytes("utf-8"))).build();

//                                glideUrl = new GlideUrl(host, new LazyHeaders.Builder().addHeader(Label
//                                        .LABEL_PATH, Base64.getEncoder().encode(nasPath.getBytes("utf-8")));
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
                        }
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
