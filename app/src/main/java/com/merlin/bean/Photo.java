package com.merlin.bean;

public final class Photo {
    private String title;
    private Object imageUrl;
    private long createTime;
    private long time;
    private String account;
    private String address;
    private String note;
    private float longitude;
    private float latitude;
    public Photo(){
        this(null,null);
    }
    public Photo(String title,Object imageUrl){
        this.title=title;
        this.imageUrl=imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getTime() {
        return time;
    }

    public String getAccount() {
        return account;
    }

    public String getAddress() {
        return address;
    }

    public Object getImageUrl() {
        return imageUrl;
    }

    public String getNote() {
        return note;
    }
}
