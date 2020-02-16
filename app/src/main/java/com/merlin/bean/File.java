package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.player.Playable;

public class File implements Parcelable, Playable {
    private long id;
    private String path;
    private String thumbImageUrl;
    private String md5;
    private boolean favorite;
    private double loadTime;
    private String mime;
    private String extension;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getMd5() {
        return md5;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public long getId() {
        return id;
    }

    public long getLoadTime() {
        return (long)loadTime;
    }

    public String getExtension() {
        return extension;
    }

    public String getMime() {
        return mime;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getName(){
        String path= this.path;
        int length=null!=path?path.length():-1;
        if (length>0){
            int index=path.lastIndexOf("/");
            index = index>=0?index:path.lastIndexOf("\\");
            String name= index>=0&&index<length?path.substring(index+1):null;
            return name;
        }
        return null;
    }

    private File(Parcel in){
        id=in.readLong();
        path=in.readString();
        thumbImageUrl=in.readString();
        md5=in.readString();
        favorite=in.readInt()==1;
        loadTime=in.readDouble();
        mime=in.readString();
        extension=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(thumbImageUrl);
        dest.writeString(md5);
        dest.writeInt(favorite?1:0);
        dest.writeDouble(loadTime);
        dest.writeString(mime);
        dest.writeString(extension);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };
}
