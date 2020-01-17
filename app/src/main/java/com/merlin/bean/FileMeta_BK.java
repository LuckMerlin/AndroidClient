package com.merlin.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import android.util.Base64;

public class FileMeta_BK {
    /**
     * file : F:\LuckMerlin\SLManager\.git
     * size : 4096
     * lastModifyTime : 1.5758874842258315E9
     * name : .git
     * directory : true
     * read : true
     * write : true
     * extension : .py
     */

    private String file;
    private int size;
    private double lastModifyTime;
    private String name;
    private boolean directory;
    private boolean read;
    private boolean write;
    private String extension;
    private int length;
    private Object thumbnail;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(double lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setThumbnail(Object thumbnail) {
        if (null==thumbnail||thumbnail instanceof String || thumbnail instanceof Bitmap) {
            this.thumbnail = thumbnail;
        }
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isRead() {
        return read;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public final  boolean isPathEquals(String path){
        String filePath=file;
        return null!=path&&null!=filePath&&filePath.equals(path);
    }

    public Bitmap getThumbnail(){
        Object data=thumbnail;
        if (null!=data){
            if(data instanceof Bitmap){
                return (Bitmap)data;
            }else if (data instanceof String){
                try {
                    byte[] bytes=((String)data).getBytes("utf-8");
                    bytes=null!=bytes&&bytes.length>0?Base64.decode(bytes,0):null;
                    if (null!=bytes&&bytes.length>0){
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        thumbnail=bitmap;
                        return bitmap;
                    }
                } catch (UnsupportedEncodingException e) {
                    //DO nothing
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileMeta_BK){
            return isPathEquals(((FileMeta_BK)obj).getFile());
        }
        return super.equals(obj);
    }

}
