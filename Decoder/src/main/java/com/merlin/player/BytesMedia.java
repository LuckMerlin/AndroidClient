package com.merlin.player;

import java.io.IOException;

public interface BytesMedia extends Media {
    int read(byte[] buffer, int offset,long loadCursor) throws IOException;
}
