package com.merlin.player;

public interface Playable {
    boolean open();
    boolean isOpened();
    int read(long start,int offset,byte[] buffer);
    boolean close();
    Meta getMeta();
}
