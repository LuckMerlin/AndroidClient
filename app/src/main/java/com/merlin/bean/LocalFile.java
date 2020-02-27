package com.merlin.bean;

public class LocalFile extends FileMeta{

    @Override
    public boolean isAccessible() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
