package com.merlin.media;

public class Sheet {
    private String mTitle;
    private long mId;
    private long mCreateTime;
    private long mCreateUserId;
    private String mCover;
    private long mLength;

    public Sheet(String title){
        mTitle=title;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public long getCreateUserId() {
        return mCreateUserId;
    }

    public long getId() {
        return mId;
    }

    public long getLength() {
        return mLength;
    }

    public String getCover() {
        return mCover;
    }

    public String getTitle() {
        return mTitle;
    }
}
