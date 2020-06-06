package com.merlin.player;

import com.merlin.id3.ID3;

public final class ID3v2 implements ID3 {
    private final byte mVersion;
    private final byte mSubVersion;
    private final byte mFlag;

    public ID3v2(byte version,byte subVersion,byte flag){
        mVersion=version;
        mSubVersion=subVersion;
        mFlag=flag;
    }

    public byte getVersion() {
        return mVersion;
    }

    public byte getSubVersion() {
        return mSubVersion;
    }

    public byte getFlag() {
        return mFlag;
    }
}
