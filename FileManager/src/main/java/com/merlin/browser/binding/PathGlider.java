package com.merlin.browser.binding;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.merlin.api.Label;
import com.merlin.api.Res;
import com.merlin.bean.Path;
import com.merlin.browser.FileDefaultThumb;
import com.merlin.click.Clicker;
import com.merlin.debug.Debug;
import com.merlin.file.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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

    public final boolean glide(View view, Object object, boolean background){
        if (null!=object&&null!=view){
            set(view,null,background);
            if (object instanceof Integer){
                if (background){
                    view.setBackgroundResource((Integer)object);
                }else if (view instanceof ImageView){
                    Clicker.putRes(view, new Res((Integer)object,null));
                    ((ImageView)view).setImageResource((Integer)object);
                }
                return true;
            }
            if (object instanceof Drawable){
                set(view,(Drawable)object,background);
            }else if (object instanceof Path&&view instanceof ImageView){
                Path path=(Path)object;
                if (path.isDirectory()){
                    ((ImageView)view).setImageResource(R.drawable.hidisk_icon_folder);
                    return false;
                }
                String mime=path.getMime();
                Debug.D(getClass(),"DDDDDDDDDDd "+mime);
                Integer iconId=null;
                if (null == mime || mime.length() <= 0) {
                    iconId = R.drawable.hidisk_icon_unknown;
                }else{
                    iconId=new FileDefaultThumb().thumb(mime);
                }
                if (null!=mime&&mime.length()>0){
                    String pathValue=path.getPath();
                    if (null!=pathValue&&pathValue.length()>0){
                        if (mime.endsWith("application/vnd.android.package-archive")) {
                            if (path.isLocal()){
                                Context context=view.getContext();
                                PackageManager manager = null!=context?context.getPackageManager():null;
                                PackageInfo packageInfo = null!=manager?manager.getPackageArchiveInfo(pathValue, PackageManager.GET_ACTIVITIES):null;
                                if (packageInfo != null) {
                                    ApplicationInfo info = packageInfo.applicationInfo;
                                    info.sourceDir = pathValue;
                                    info.publicSourceDir = pathValue;
                                    try {
                                        ((ImageView)view).setImageDrawable(info.loadIcon(manager));
                                        return true;
                                    } catch (Exception e) {
                                        //Do nothing
                                    }
                                }
                            }else{
                                return loadCloudFileThumb((ImageView)view, path);
                            }
                        }else if (mime.startsWith("image/")||mime.startsWith("video/")){
                              if (path.isLocal()){
                                  File localFile=null!=pathValue&&pathValue.length()>0?new File(pathValue):null;
                                  if (null!=localFile){
                                      Glide.with(view).load(localFile).into((ImageView)view);
                                      return true;
                                  }
                              }else{
                                  return loadCloudFileThumb((ImageView)view, path);
                              }
                        }
                    }
                }
                ((ImageView)view).setImageResource(null!=iconId?iconId:R.drawable.hidisk_icon_unknown);
                return true;
            }
//            Context context=view.getContext();
//            if (null!=path&&path instanceof Path &&null!=context){
//                Path imagePath=(Path)path;
//                String nasPath=imagePath.getPath();
//                String host=imagePath.getHost();
//                int port=imagePath.getPort();
//                Debug.D(getClass(),"AAAAAAAAAAAA "+nasPath+" "+host+" "+port);
//                RequestBuilder<Drawable> builder=null;
//                if (null!=nasPath&&nasPath.length()>0){
//                    if (null!=host&&host.length()>0&&!imagePath.isLocal()){
//                        GlideUrl glideUrl = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                            try {
////                                new LazyHeaders.Builder().addHeader(Label.LABEL_PATH, Base64.getEncoder()
////                                        .encode(nasPath.getBytes("utf-8"))).build();
//
////                                glideUrl = new GlideUrl(host, new LazyHeaders.Builder().addHeader(Label
////                                        .LABEL_PATH, Base64.getEncoder().encode(nasPath.getBytes("utf-8")));
////                            } catch (UnsupportedEncodingException e) {
////                                e.printStackTrace();
////                            }
//                        }
//                        builder= Glide.with(context).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE);
//                    }else{
//                        builder=Glide.with(context).load(new File(nasPath));
//                    }
//                }
//                if (null!=builder){
//                    RoundedCorners roundedCorners = new RoundedCorners(1);
//                    RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(view.getWidth(),
//                            view.getHeight());
//                    CustomTarget<Drawable> simpleTarget = new CustomTarget<Drawable>() {
//                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                        @Override
//                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                            set(view,resource,background);
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                        }
//                    };
//
//                    builder.centerCrop().apply(options).thumbnail(1f)
////                                .transform(new BlurMaskFilter)
////                                .bitmapTransform(new BlurTransformation(context, 5),//模糊转换
////                                new TopCropTransformation(context))
//                                .into(simpleTarget);
//                }
//            }
            return false;
        }
        return false;
    }

    private boolean loadCloudFileThumb(ImageView view, Path path){
        String hostUri=null!=path&&null!=view?path.getHostUri():null;
        if (null!=hostUri&&hostUri.length()>0){
            String filePath=path.getPath();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    //ByteArrayOutputStream dd=new ByteArrayOutputStream();
                    int width = view.getWidth();
                    int height = view.getHeight();
                    GlideUrl glideUrl = new GlideUrl(hostUri, new LazyHeaders.Builder()
                            .addHeader(Label.LABEL_PATH, URLEncoder.encode(filePath,"utf-8")).addHeader(Label.LABEL_WIDTH,
                            Integer.toString(width)).addHeader(Label.LABEL_HEIGHT, Integer.toString(height)).build());
                    if (null != glideUrl) {
                        RoundedCorners roundedCorners = new RoundedCorners(1);
                        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(width, height);
                        Glide.with(view.getContext()).load(glideUrl).diskCacheStrategy(DiskCacheStrategy.NONE).
                                centerCrop().apply(options).thumbnail(1f).into(view);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
