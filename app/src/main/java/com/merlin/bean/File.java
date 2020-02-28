package com.merlin.bean;

import androidx.annotation.Nullable;

import com.merlin.debug.Debug;

public class File implements FileMeta {
    private String parent;
    private String name;
    private String title;
    private String extension;
    private Object imageUrl;
    private long size;
    private double modifyTime;
    private boolean directory;
    private boolean accessible;

    public File() {
        this(null,null,null,null,null,0,0,false,false);
    }

    public File(String parent,String title,String name,String extension,
                String imageUrl,long size,long modifyTime, boolean directory, boolean accessible) {
        this.parent=parent;
        this.title=title;
        this.name=name;
        this.extension=extension;
        this.imageUrl=imageUrl;
        this.size=size;
        this.modifyTime=modifyTime;
        this.directory=directory;
        this.accessible=accessible;
    }

    @Override
    public boolean applyModify(FModify modify) {
        if (null!=modify){
            String name=modify.getName();
            if (null!=name){
                this.name=name;
            }
            String extension=modify.getExtension();
            if (null!=extension){
                this.extension=extension;
            }
            return true;
        }
        return false;
    }

    @Override
    public Object getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public double getModifyTime() {
        return modifyTime;
    }

    @Override
    public String getPath() {
        String value=getName(true);
        return null!=parent&&null!=value?parent+value:null;
    }

    @Override
    public boolean isAccessible() {
        return accessible;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public String permission() {
        return "";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof File){
            File file=(File)obj;
            return equals(file.parent,parent)&&equals(file.name,name)&&equals(file.extension,extension);
        }
        return super.equals(obj);
    }

    private boolean equals(String v1,String v2){
        return null!=v1&&null!=v2?v1.equals(v2):(null==v1&&null==v2);
    }

}
