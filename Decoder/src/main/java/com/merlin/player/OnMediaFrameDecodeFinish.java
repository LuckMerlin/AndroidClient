package com.merlin.player;

public interface OnMediaFrameDecodeFinish {
    int MEDIA_TYPE_AUDIO =1;
    int MEDIA_TYPE_AUDIO_STREAM =2;
    int MEDIA_TYPE_VIDEO =3;
    void onMediaFrameDecodeFinish(int mediaType,byte[] bytes,int channels,int sampleRate);
}
