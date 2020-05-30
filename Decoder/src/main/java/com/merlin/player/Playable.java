package com.merlin.player;

import java.io.IOException;
import java.io.InputStream;

public interface Playable {
    interface OnReady{
        void onReady(int what,InputStream inputStream);
    }
    boolean open();
    boolean isOpened();
    Integer read(byte[] buffer, int offset) throws IOException;
    boolean close();
    Meta getMeta();
}
