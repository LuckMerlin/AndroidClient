package com.merlin.bean;

import java.util.List;

public final class Love {
    public final static int MODE_CANCELED=1232;
    private final String name;
    private final String data;
    private List<Photo> image;
    private final long id;
    private int mode=0;
    private String account;
    private long createTime;
    private long time;

    public Love(){
        this(0,null,null);
    }

    public Love(long id,String name,String data){
        this.name=name;
        this.id=id;
        this.data=data;
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
