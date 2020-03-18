package com.merlin.bean;

import java.util.List;

public final class Love {
    public final static int MODE_CANCELED=1232;
    private String name;
    private String data;
    private List<Photo> image;
    private long id;
    private int mode=0;
    private String account;
    private long createTime;
    private long time;

    public Love(){
        this(null,0,null,null);
    }

    public Love(String name,long time,String content,List<Photo> photos){
        this.name=name;
        this.time=time;
        this.data=content;
        this.image=photos;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public int getMode() {
        return mode;
    }

    public long getTime() {
        return time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getAccount() {
        return account;
    }

    public long getId() {
        return id;
    }

    public List<Photo> getImage() {
        return image;
    }
}
