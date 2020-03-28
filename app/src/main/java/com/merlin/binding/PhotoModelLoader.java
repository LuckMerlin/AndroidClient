package com.merlin.binding;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.merlin.debug.Debug;

import java.io.InputStream;

public class PhotoModelLoader extends BaseGlideUrlLoader<ViewDataLoadable> {

    public static class Factory implements ModelLoaderFactory<ViewDataLoadable, InputStream> {

        private final ModelCache<ViewDataLoadable, GlideUrl> modelCache = new ModelCache<ViewDataLoadable, GlideUrl>(500);

        @Override
        public ModelLoader<ViewDataLoadable, InputStream> build(MultiModelLoaderFactory multiFactory) {
            PhotoModelLoader loader=new PhotoModelLoader(multiFactory.build(GlideUrl.class, InputStream.class),modelCache);
            return loader;
        }

        @Override
        public void teardown() {
            Debug.D(getClass(),"AAAAAAA teardown AAAAA ");
        }
    }

    protected PhotoModelLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }

    protected PhotoModelLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, ModelCache<ViewDataLoadable, GlideUrl> modelCache) {
        super(concreteLoader, modelCache);
    }

    @Override
    protected String getUrl(ViewDataLoadable loadable, int width, int height, Options options) {
        Debug.D(getClass(),"AAAAAAA getUrl AAAAA "+loadable);
        return null;
    }

    @Override
    public boolean handles(ViewDataLoadable photo) {
        Debug.D(getClass(),"AAAAAAA handles AAAAA "+photo );
        return true;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull ViewDataLoadable loadable, int width, int height, @NonNull Options options) {
        return super.buildLoadData(loadable, width, height, options);
    }



}