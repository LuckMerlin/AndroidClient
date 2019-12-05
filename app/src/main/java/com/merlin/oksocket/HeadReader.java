package com.merlin.oksocket;

import com.merlibn.global.Protocol;
import com.merlin.debug.Debug;


public final class HeadReader {
    public final static int HEAD_LENGTH =12; //1+1+5+5
    private final byte mCode;
    private final byte mEncoding;
    private final int mBodySize;
    private final int mHeadSize;
    private final int mContentSize;

    private HeadReader(byte code, byte encoding, int bodySize, int headSize, int contentSize){
        mCode=code;
        mEncoding=encoding;
        mBodySize=bodySize;
        mHeadSize=headSize;
        mContentSize=contentSize;
    }

    public static HeadReader read(byte[] bytes, boolean bigOrder){
        if (null!=bytes&&bytes.length>=HEAD_LENGTH) {
            byte code =bytes[0];
            byte encoding=bytes[1];
            Long headLength = Protocol.bytes2Long(bytes, 0, bigOrder, 2, 7);
            Long contentLength = Protocol.bytes2Long(bytes, 0, bigOrder, 7, 12);
            if (null==headLength||null==contentLength){
                Debug.W(HeadReader.class,"Can't build head,Length is invalid."+headLength+" "+contentLength);
                return null;
            }
            int head=(int)((long) headLength);
            int content=(int)((long) contentLength);
            return new HeadReader(code,encoding,(head+content),head,content);
        }
        return null;
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
