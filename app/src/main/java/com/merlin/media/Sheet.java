package com.merlin.media;

public class Sheet {
    /**
     * name : 我最喜欢的歌单
     * size : 123
     * cover : null
     * id : 1231321
     * create : 2342
     */

    private String name;
    private int size;
    private String cover;
    private int id;
    private int create;

    public Sheet(){
        this(null);
    }

    public Sheet(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreate() {
        return create;
    }

    public void setCreate(int create) {
        this.create = create;
    }
}
