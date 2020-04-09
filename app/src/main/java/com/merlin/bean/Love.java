package com.merlin.bean;

import java.util.List;

public final class Love {
    private String name;
    private String data;
    private List<Path> image;
    private long id;
    private String mode;
    private String account;
    private long createTime;
    private long time;

    public Love(){
        this(null,0,null,null);
    }

    public Love(String name,long time,String content,List<Path> photos){
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

    public String getMode() {
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

    public List<Path> getImage() {
        return image;
    }
}
