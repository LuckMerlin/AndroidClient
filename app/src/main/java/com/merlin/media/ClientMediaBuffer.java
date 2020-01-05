package com.merlin.media;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.player.MediaBuffer;

public class ClientMediaBuffer implements MediaBuffer {
    private final Client mClient;

    public ClientMediaBuffer(Client client){
        mClient=client;
    }

    @Override
    public Canceler buffer(String account, String url, String target,OnMediaBufferFinish callback) {
        Client client=mClient;
        if (null==client){
            Debug.W(getClass(),"Can't buffer media.client="+client+" "+url);
            return null;
        }
        if (null==url||url.isEmpty()||null==target||target.isEmpty()){
            Debug.W(getClass(),"Can't buffer media.target="+target+" url="+url);
            return null;
        }
        if (!client.isLogined()){
            Debug.W(getClass(),"Can't buffer media,Client not login.");
            return null;
        }
        Client.Canceler canceler= client.downloadFile(account,url,target, (finish, what, accountValue, urlValue, to, data)->{
                    Debug.D(getClass(),"#### "+finish+" "+what);
                    if (finish&&what== Client.OnFileDownloadUpdate.DOWNLOAD_SUCCEED){
                        callback.onMediaBufferFinish(true,OnMediaBufferFinish.BUFFER_SUCCEED,account,url,target);
                    }
                });
        return null!=canceler?(interrupt)->canceler.cancel(interrupt):null;
    }
}
