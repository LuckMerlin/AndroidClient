package com.merlin.oksocket;


public final class Head {
    private final byte mCode;
    private final byte mEncoding;
    private final int mBodySize;
    private final int mHeadSize;
    private final int mContentSize;
    private final int mMsgFromSize;
    private final int mMsgToSize;

    public Head(byte code, byte encoding, int bodySize,int msgFromSize,int msgToSize, int headSize, int contentSize){
        mCode=code;
        mEncoding=encoding;
        mMsgFromSize=msgFromSize;
        mMsgToSize=msgToSize;
        mBodySize=bodySize;
        mHeadSize=headSize;
        mContentSize=contentSize;
    }

    public byte getCode() {
        return mCode;
    }

    public byte getEncoding() {
        return mEncoding;
    }

    public int getBodySize() {
        return mBodySize;
    }

    public int getMsgFromSize() {
        return mMsgFromSize;
    }

    public int getMsgToSize() {
        return mMsgToSize;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public int getHeadSize() {
        return mHeadSize;
    }

    public String getEncodingName(){
        return mEncoding==1?"utf-8":"utf-8";
    }
}
