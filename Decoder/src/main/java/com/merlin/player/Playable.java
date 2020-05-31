package com.merlin.player;

import java.io.IOException;
import java.io.InputStream;

public interface Playable {
    interface CacheReady{
        void onCacheReady(int what,InputStream inputStream,long total) throws IOException;
    }
    boolean open();
    boolean isOpened();
    boolean close();
    boolean cache(CacheReady cacheReady) throws IOException;
    Meta getMeta();
}
