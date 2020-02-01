package com.merlin.bean;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.file.Permissions;

import static com.merlin.api.What.WHAT_NOT_DIRECTORY;

public final class FileMeta implements Parcelable {
    private long id;
    private String path;
    private String name;
    private String md5;
    private String mime;
    private String extension;
    private double createTime;
    private double modifyTime;
    private int permissions;
    private long length;
    private double insertTime;
    private int childCount;
    private boolean favorite;
    private String extra;
    private String thumbImageUrl;
    private String title;
    private Media meta;

    public String getName() {
        return name;
    }

    public boolean isDirectory(){
        return childCount!=WHAT_NOT_DIRECTORY;
    }

    public long getLength() {
        return length;
    }

    public String getExtension() {
        return extension;
    }

    public double getModifyTime() {
        return modifyTime*1000;
    }

    public String getPath() {
        return path;
    }

    public int getChildCount() {
        return childCount;
    }

    public String getTitle() {
        return title;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public  boolean isAccessible(){
        int permission=permissions;
        Permissions permissions=new Permissions();
        return permissions.isOtherReadable(permission)&&(!isDirectory()||permissions.isOtherExecutable(permission));
    }

    public int getPermissions() {
        return permissions;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Media getMeta() {
        return meta;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getMd5() {
        return md5;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @SuppressLint("NewApi")
    private FileMeta(Parcel in){
        id=in.readLong();
        length=in.readLong();
        path=in.readString();
        name=in.readString();
        title=in.readString();
        md5=in.readString();
        mime=in.readString();
        extension=in.readString();
        extra=in.readString();
        thumbImageUrl=in.readString();
        createTime=in.readDouble();
        modifyTime=in.readDouble();
        insertTime=in.readDouble();
        permissions=in.readInt();
        childCount=in.readInt();
        favorite=in.readBoolean();
        Parcelable parcelable=in.readParcelable(FileMeta.class.getClassLoader());
        meta=null!=parcelable&&parcelable instanceof Media?(Media)parcelable:null;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(length);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(md5);
        dest.writeString(mime);
        dest.writeString(extension);
        dest.writeString(extra);
        dest.writeString(thumbImageUrl);
        dest.writeDouble(createTime);
        dest.writeDouble(modifyTime);
        dest.writeDouble(insertTime);
        dest.writeInt(permissions);
        dest.writeInt(childCount);
        dest.writeBoolean(favorite);
        dest.writeParcelable(meta,0);

    }

    public static final Creator<FileMeta> CREATOR = new Creator<FileMeta>() {
        @Override
        public FileMeta createFromParcel(Parcel in) {
            return new FileMeta(in);
        }

        @Override
        public FileMeta[] newArray(int size) {
            return new FileMeta[size];
        }
    };


}
