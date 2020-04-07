package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Path implements Parcelable {
    private String parent;
    private String name;
    private String extension;
    private String host;

    public Path(){
        this(null,null,null);
    }

    public Path(String parent,String name,String extension){
        this(null,parent,name,extension);
    }

    public Path(String host,String parent,String name,String extension){
        this.host=host;
        this.parent=parent;
        this.name=name;
        this.extension=extension;
    }

    public final String getExtension() {
        return extension;
    }

    public final String getParent() {
        return parent;
    }

    public final String getName() {
        return getName(false);
    }

    public final String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    public final String getHost() {
        return host;
    }

    public final String getPath() {
        return getPath(null);
    }

    public final String getPath(String hostDivider) {
        String value=getName(true);
        String path=null!=parent&&null!=value?parent+value:null;
        String host=null!=hostDivider?this.host:null;
        return null!=host?host+(hostDivider.length()<=0?"/":hostDivider):path;
    }

    private Path apply(String host,String parent,String name,String extension){
        return this;
    }

    public static <T extends Path> Path build(Object object,T result){
        if (null!=object){
            if (object instanceof File){
                File file=(File)object;
                String parent=file.getParent();
                parent=null!=parent?parent+ java.io.File.separator:null;
                String name=null!=file?file.getName():null;
                int index=null!=name?name.lastIndexOf("."):-1;
                String extension=index>0?name.substring(index):null;
                name=index>0?name.substring(0,index):name;
                return (null==result?new Path():result).apply(null,parent,name,extension);
            }
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parent);
        dest.writeString(name);
        dest.writeString(extension);
        dest.writeString(host);
    }

    protected Path(Parcel parcel){
        parent=parcel.readString();
        name=parcel.readString();
        extension=parcel.readString();
        host=parcel.readString();

    }

    public static final Creator<Path> CREATOR = new Creator<Path>() {
        @Override
        public Path createFromParcel(Parcel in) {
            return new Path(in);
        }

        @Override
        public Path[] newArray(int size) {
            return new Path[size];
        }
    };

}
