package com.merlin.player1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.merlin.debug.Debug;
import com.merlin.player.OnMediaFrameDecodeFinish;
import com.merlin.player.Player;
import com.merlin.util.FileMaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MPlayer extends Player implements OnMediaFrameDecodeFinish {
    private AudioTrack mAudioTrack;

    public MPlayer(){
        setOnDecodeFinishListener(this);
    }

    @Override
    public void onMediaFrameDecodeFinish(int mediaType, byte[] bytes, int channels, int sampleRate) {
        final int length=null!=bytes?bytes.length:-1;
        if (length<=0){//Invalid frame data
            return;
        }
        AudioTrack audioTrack=buildAudioTrack(sampleRate,AudioFormat.CHANNEL_IN_STEREO);
        if (null==audioTrack){
            Debug.W(getClass(),"Can't play media frame. sampleRate="+sampleRate);
            return;
        }
        if (audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
            audioTrack.play();
        }
        audioTrack.write(bytes,0,length);
    }

    private AudioTrack buildAudioTrack(int sampleRateInHz,int channelConfig){
        if (sampleRateInHz<=0||channelConfig<=0){
            Debug.W(getClass(),"Can't build audio track.sampleRate="+sampleRateInHz+" channelConfig="+channelConfig);
            return null;
        }
        AudioTrack audioTrack=mAudioTrack;
        if (null!=audioTrack){
            int currSampleRateInHz=audioTrack.getSampleRate();
            int currChannelConfig=audioTrack.getChannelConfiguration();
            if (sampleRateInHz==currSampleRateInHz&&channelConfig==currChannelConfig){
                return audioTrack;
            }
            Debug.W(getClass(),"Release existed audio track when sampleRate changed."+
                    currSampleRateInHz+" "+sampleRateInHz+" "+currChannelConfig+" "+channelConfig);
            mAudioTrack=null;
            audioTrack.stop();
            audioTrack.release();
        }
        mAudioTrack=audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig, AudioFormat.ENCODING_PCM_16BIT),AudioTrack.MODE_STREAM);
        return audioTrack;
    }

    private boolean play(String path,byte[] bytes,int len,long contentLength){
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't play media bytes with invalid path.path="+path);
            return false;
        }
        int length=null!=bytes?bytes.length:-1;
        if (length<=0||contentLength<=0){
            Debug.W(getClass(),"Can't play media bytes with invalid bytes.length="+length+" contentLength="+contentLength);
            return false;
        }
        final File file=new FileMaker().makeFile(path);
        if (null==file||!file.exists()){
            Debug.W(getClass(),"Can't play media bytes,Cache file open fail."+file);
            return false;
        }
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file,true);
            fos.write(bytes,0,len);
        } catch (Exception e) {
            Debug.E(getClass(),"Can't play media bytes,Cache file write exception.e="+e+" "+file,e);
        }finally {
            if (null!=fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    //Do nothing
                }
            }
        }
//        if (file.exists()){
//
//        }
        return false;
    }


}
