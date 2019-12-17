package com.merlin.player1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.merlin.debug.Debug;
import com.merlin.server.Frame;

public class MediaPlayer {
    private AudioTrack audioTrack;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaPlayer(){
        int samplerate = 44100 ;//44100;
        int bufferSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
////        int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
////        int bufferSizeInBytes, int mode
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                samplerate, AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);
//        audioTrack.play();
//        audioTrack.setVolume(0.12f);
    }

    public boolean play(Frame frame){
        byte[] bytes=null!=frame?frame.getBodyBytes():null;
        return play(bytes,0, bytes.length);
    }

    public boolean play(byte[] bytes, int off, int length){
        if (null!=bytes&&bytes.length>0) {
//            Debug.D(getClass(), "### 播放  " + bytes.length);
            audioTrack.write(bytes, off, length);
        }else{
            Debug.D(getClass(),"错误 "+bytes);
        }
        return false;
    }
}
