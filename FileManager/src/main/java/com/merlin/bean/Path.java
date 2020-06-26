package com.merlin.bean;

public class Path {
    private String name;
    private String parent;
    private int permissions;
    private String host;
    private int port;
    private int size;
    private String extension;
    private String mime;
    private int length;
    private int accessTime;
    private int modifyTime;
    private int createTime;
    private String md5;

    public final String getName(boolean format) {
        return name;
    }

    public final String getName() {
        return getName(true);
    }

    public String getParent() {
        return parent;
    }

    public int getPermissions() {
        return permissions;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getSize() {
        return size;
    }

    public String getExtension() {
        return extension;
    }

    public String getMime() {
        return mime;
    }

    public int getLength() {
        return length;
    }

    public int getAccessTime() {
        return accessTime;
    }

    public final int getModifyTime() {
        return modifyTime;
    }

    public int getCreateTime() {
        return createTime;
    }

    public final String getMd5() {
        return md5;
    }

    public final boolean isDirectory(){
        return size>=0;
    }

    public final String getPath() {
        return name;
    }

    public final boolean isAccessible() {
        return true;
    }

    public final boolean isLocal() {
        return false;
    }
}
