package com.merlin.bean;

public class LocalFile extends FileMeta{

    public LocalFile(String path,String name){
        super(path,name);
    }

    @Override
    public boolean isAccessible() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
