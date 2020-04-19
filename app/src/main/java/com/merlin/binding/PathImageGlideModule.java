package com.merlin.binding;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class PathImageGlideModule extends AppGlideModule {
    public static final int DISK_CACHE_SIZE = 500 * 1024 * 1024;
    public static final String DISK_CACHE_NAME = "huangxiaoguo";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder); /**
         将所有Glide加载的图片缓存到SD卡上,
         默认硬盘缓存大小都是250M,这里改为500
         * */ //builder.setDiskCache(new ExternalCacheDiskCacheFactory(context)); /**
//        ExternalCacheDiskCacheFactory的默认缓存路径
//        是在sdcard / Android / data / 包名 / cache / image_manager_disk_cache目录当中
//                */ //builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE)); /**
//         *更改缓存最总文件夹名称
//                *
//         *是在sdcard / Android / data / 包名 / cache / DISK_CACHE_NAME目录当中
//                */ //此方法在Glide4已过时 // builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, DISK_CACHE_NAME, DISK_CACHE_SIZE)); builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, DISK_CACHE_NAME, DISK_CACHE_SIZE)); /**
//         *Glide也能使用ARGB_8888的图片格式
//                * 虽然图片质量变好了，但同时内存开销也会明显增大
//                */
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, DISK_CACHE_NAME, DISK_CACHE_SIZE));
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

    }

}
