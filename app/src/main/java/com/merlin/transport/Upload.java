package com.merlin.transport;

import com.merlin.bean.ClientMeta;

public final class Upload extends Transport {
    private final String mPath;
    private final String mFolder;
    private final ClientMeta mMeta;

    public Upload(String path,String folder,String name,ClientMeta meta,Integer coverMode){
        super(name,meta,coverMode);
        mPath=path;
        mFolder=folder;
        mMeta=meta;
    }

    public ClientMeta getMeta() {
        return mMeta;
    }


    public String getFolder() {
        return mFolder;
    }

    public String getPath() {
        return mPath;
    }

}
