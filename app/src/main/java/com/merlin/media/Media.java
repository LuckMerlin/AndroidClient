package com.merlin.media;

public class Media {
    private final String mTitle;
    private final String mUrl;
    private final String mAccount;

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
}
