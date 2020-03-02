package com.merlin.transport;

import com.merlin.bean.ClientMeta;

public final class Upload implements Transport {
    private final String mPath;
    private final String mFolder;
    private final String mName;
    private final ClientMeta mMeta;

    public Upload(String path,String folder,String name,ClientMeta meta){
        mPath=path;
        mFolder=folder;
        mName=name;
        mMeta=meta;
    }

    public ClientMeta getMeta() {
        return mMeta;
    }

    public String getName() {
        return mName;
    }

    public String getFolder() {
        return mFolder;
    }

    public String getPath() {
        return mPath;
    }

    @Override
    public String getTitle() {
        return mName;
    }
}
