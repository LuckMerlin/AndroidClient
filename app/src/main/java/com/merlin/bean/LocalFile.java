package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.merlin.api.What;

public final class LocalFile implements Parcelable,FileMeta {
    private String parent;
    private String name;
    private String title;
    private String extension;
    private Object imageUrl;
    private long size;
    private int childCount;
    private double modifyTime;
    private boolean directory;
    private boolean accessible;

    public LocalFile(String parent,String title,String name,String extension,
                     String imageUrl,int childCount,long size,long modifyTime, boolean directory, boolean accessible){
        this.parent=parent;
        this.title=title;
        this.name=name;
        this.childCount=childCount;
        this.extension=extension;
        this.imageUrl=imageUrl;
        this.size=size;
        this.modifyTime=modifyTime;
        this.directory=directory;
        this.accessible=accessible;
    }

    public static LocalFile create(java.io.File file, String imageUrl){
        if (null!=file){
            String parent=file.getParent();
            parent=null!=parent?parent+ java.io.File.separator:null;
            String[] fix=getPostfix(file);
            String name=fix[0];
            String extension=fix[1];
            String title=file.getName();
            boolean directory=file.isDirectory();
            long size=file.length();
            int childCount= What.WHAT_NOT_DIRECTORY;
            if (directory){
                String[] names=file.list();
                childCount=null!=names?names.length:0;
            }
            long modifyTime=file.lastModified();
            boolean accessible=file.canRead()&&(!file.isDirectory()||file.canExecute());
            return new LocalFile(parent,title,name,extension,imageUrl,childCount,size,modifyTime,directory,accessible);
        }
        return null;
    }

    public java.io.File getFile(){
        String path= getPath();
        return null!=path&&path.length()>0?new java.io.File(path):null;
    }

    public static String[] getPostfix(java.io.File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf("."):-1;
        String[] result=new String[2];
        if (index>0){
            result[0]=name.substring(0,index);
            result[1]=name.substring(index);
        }else{
            result[0]=name;
        }
        return result;
    }

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

    public Object getImageUrl() {
        return imageUrl;
    }

    public String getParent() {
        return parent;
    }

    public String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    public String getTitle() {
        return title;
    }

    public String getExtension() {
        return extension;
    }

    public long getSize() {
        return size;
    }

    public double getModifyTime() {
        return modifyTime;
    }

    public String getPath() {
        String value=getName(true);
        return null!=parent&&null!=value?parent+value:null;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String permission() {
        return "";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof LocalFile){
            LocalFile file=(LocalFile)obj;
            return equals(file.parent,parent)&&equals(file.name,name)&&equals(file.extension,extension);
        }
        return super.equals(obj);
    }

    private boolean equals(String v1,String v2){
        return null!=v1&&null!=v2?v1.equals(v2):(null==v1&&null==v2);
    }

    @Override
    public int getChildCount() {
        return childCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        parent=dest.readString();
//        name=dest.readString();
//        title=dest.readString();
//        extension=dest.readString();
//        imageUrl=dest.readString();
//        size=dest.readLong();
//        modifyTime=dest.readDouble();
//        directory=dest.readInt()==1;
//        accessible=dest.readInt()==1;
    }

    private LocalFile(Parcel in){
        in.writeString(parent);
//        in.writeString(name);
//        in.writeString(title);
//        in.writeString(extension);
//        in.writeString(null!=imageUrl&&imageUrl instanceof String?(String)imageUrl:"");
//        in.writeLong(size);
//        in.writeDouble(modifyTime);
//        in.writeInt(directory?1:0);
//        in.writeInt(accessible?1:0);
    }

    public static final Creator<LocalFile> CREATOR = new Creator<LocalFile>() {
        public LocalFile createFromParcel(Parcel in) {
            return new LocalFile(in);
        }

        @Override
        public LocalFile[] newArray(int size) {
            return new LocalFile[size];
        }
    };


}
