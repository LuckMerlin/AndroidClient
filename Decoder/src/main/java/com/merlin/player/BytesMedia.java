package com.merlin.player;

import java.io.IOException;

public interface BytesMedia extends Playable{
    int read(byte[] buffer, int offset) throws IOException;
}
