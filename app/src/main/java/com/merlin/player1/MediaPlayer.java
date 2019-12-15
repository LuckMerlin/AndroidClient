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

//    static {
//        System.load("linqiang");
//    }
//
//    public native boolean play(String path,float seek);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaPlayer(){
        int samplerate = 44100 ;//44100;
        int bufferSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
////        int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
////        int bufferSizeInBytes, int mode
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                samplerate/2, AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);
//        audioTrack.play();
////        int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
////        int bufferSizeInBytes, int mode
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
//                // STREAM_ALARM：警告声
//                // STREAM_MUSCI：音乐声，例如music等
//                // STREAM_RING：铃声
//                // STREAM_SYSTEM：系统声音
//                // STREAM_VOCIE_CALL：电话声音
//                samplerate,// 设置音频数据的采样率
//                AudioFormat.CHANNEL_CONFIGURATION_STEREO,// 设置输出声道为双声道立体声
//                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
//                bufferSize, AudioTrack.MODE_STREAM);// 设置模式类型，在这里设置为流类型
        audioTrack.play();
        audioTrack.setVolume(0.02f);
    }

    public boolean play(Frame frame){
        byte[] bytes=null!=frame?frame.getBodyBytes():null;
        return play(bytes,0, bytes.length);
    }

    public boolean play(byte[] bytes, int off, int length){
        if (null!=bytes&&bytes.length>0) {
            Debug.D(getClass(), "### 播放  " + bytes.length);
            audioTrack.write(bytes, off, length);
        }else{
            Debug.D(getClass(),"错误 "+bytes);
        }
        return false;
    }
}
