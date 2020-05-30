package com.merlin.player;

import java.io.IOException;

public interface OnLoadMedia {
    int onLoadMedia(byte[] buffer,int offset) throws IOException, InterruptedException;
}
