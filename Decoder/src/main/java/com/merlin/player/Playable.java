package com.merlin.player;

import java.io.IOException;
import java.io.InputStream;

public interface Playable {
    interface CacheReady{
        void onCacheReady(InputStream inputStream) throws IOException;
    }
    boolean open();
    boolean isOpened();
    boolean close();
    boolean cache(CacheReady cacheReady) throws IOException;
    Meta getMeta();
}
