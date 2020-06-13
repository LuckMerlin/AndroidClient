package com.merlin.player;

import java.io.IOException;
import java.io.InputStream;

public interface Media {
    interface CacheReady{
        void onCacheReady(InputStream inputStream) throws IOException;
    }
    boolean open(Player player);
    boolean isOpened();
    boolean close(Player player);
    boolean cache(Player player,CacheReady cacheReady) throws IOException;
    Meta getMeta();
}
