package com.merlin.bean;

public abstract class FileMeta {
    private String path;
    private String name;

    public boolean applyModify(FileModify modify){
        if (null!=modify){
            String path=modify.getPath();
            String name=modify.getName();
            if (null!=path||null!=name){
                this.path=path;
                this.name=name;
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public abstract boolean isAccessible();

    public abstract boolean isDirectory();
}
