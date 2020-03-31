package com.merlin.browser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.IOException;
import java.io.InputStream;

public class LocalFileSyncDecoder  implements ResourceDecoder<InputStream, FileSync> {

    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        return false;
    }

    @Nullable
    @Override
    public Resource<FileSync> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
//        options.set()
        return null;
    }
}
