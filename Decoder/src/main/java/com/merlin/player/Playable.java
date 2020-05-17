package com.merlin.player;

public interface Playable {
    int BUFFER_READ_FINISH_EOF =-1;
    int BUFFER_READ_FINISH_INNER_ERROR= -5;
    int BUFFER_READ_FINISH_NORMAL= -2;

    boolean open();
    int read(byte[] buffer);
    boolean close();
}
