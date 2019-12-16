package com.merlin.player;

public interface OnDecodeFinish {
    void onDecodeFinish(byte[] bytes,int channels,int sampleRate);
}
