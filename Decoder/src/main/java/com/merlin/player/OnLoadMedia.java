package com.merlin.player;

import java.io.IOException;

interface OnLoadMedia {
    int onLoadMedia(byte[] buffer,int offset) throws IOException, InterruptedException;
}
