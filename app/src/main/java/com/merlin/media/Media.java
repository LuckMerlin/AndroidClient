package com.merlin.media;

public class Media {
    private final String mTitle;
    private final String mUrl;
    private final String mAccount;
    private final String mAlbum="爱你一万年";
    private final String mArtist="刘德华";
    private final long mDuration=12131;

    public Media(String account,String title,String url){
        mAccount=account;
        mTitle=title;
        mUrl=url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAccount() {
        return mAccount;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDurationText(){
        return null;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }
}
