package com.merlin.binding;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

@GlideModule
public class TestGlidle extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
//        registry.append(File.class, OutputStream.class,new PhotoModelLoader.Factory())
//        registry.append(InputStream.class, FileSync.class,new LocalFileSyncDecoder());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
