package com.merlin.media;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.player.MediaBuffer;

import java.nio.Buffer;

public class ClientMediaBuffer extends MediaBuffer<Media> {
    private final Client mClient;
    private final Boolean mMutex=true;
    private final byte[] mContent=new byte[1024];

    public ClientMediaBuffer(Client client, Media media, double seek){
        super(media,seek);
        mClient=client;
    }

    @Override
    protected boolean open(double seek, String debug) {
        Client client=mClient;
        boolean login=null!=client&&client.isLogined();
        if (!login){
            Debug.D(getClass(),"Can't play media while failed open client url,Not login "+(null!=debug?debug:".")+" client="+client);
            return false;
        }
        final Media media=getPlayable();
        String url=null!=media?media.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.D(getClass(),"Can't play media,Url invalid "+(null!=debug?debug:".")+" url="+url+" "+media);
            return false;
        }
        String account=media.getAccount();
        Debug.D(getClass(),"Play media from "+account+" "+url);
        client.downloadFile(account,url,"/sdcard/a/temp.mp3",(finish,what,fromAccount,urlAddress, toTarget,data)->{
                Debug.D(getClass(),"%%%%%%%%%% "+finish+" "+what+" ");
                synchronized (mMutex){
                    mMutex.notify();
                }
        });
        synchronized (mMutex){
            try {
                Debug.D(getClass(),"Wait for open response.");
                mMutex.wait();
                Debug.D(getClass(),"Wakeup for open response."+account+" "+url);
            } catch (InterruptedException e) {
                Debug.E(getClass(),"Can't wait for client media open response.e="+e+" "+account+" "+url,e);
            }
        }
        return false;
    }

    @Override
    protected int read(byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    protected boolean close(String debug) {
        return false;
    }

    @Override
    protected boolean seek(double seek) {
        return false;
    }

    @Override
    public boolean isOpened() {
        return false;
    }
}
