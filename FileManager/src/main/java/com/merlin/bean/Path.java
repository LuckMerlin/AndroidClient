package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.file.R;

public final class Path implements Parcelable {
    private transient Reply<Path> mSync;
    private String name;
    private String parent;
    private int permissions;
    private String host;
    private int port;
    private long size;
    private String extension;
    private String mime;
    private long length;
    private long accessTime;
    private long modifyTime;
    private long createTime;
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

    public long getSize() {
        return size;
    }

    public final String getExtension() {
        return extension;
    }

    public final String getMime() {
        return mime;
    }

    public final long getLength() {
        return length;
    }

    public final long getAccessTime() {
        return accessTime;
    }

    public final long getModifyTime() {
        return modifyTime;
    }

    public final long getCreateTime() {
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

    public int syncColor(){
        int color= R.color.syncNull;
        String md5=getMd5();
        if (null!=md5&&md5.length()>0){
            color=R.color.syncNeed;
            Reply<Path> sync=mSync;
            if (null!=sync){
                color=R.color.syncFail;
                if (sync.isSuccess()&&sync.getWhat()== What.WHAT_SUCCEED){
                    Path syncUrl=sync.getData();
                    String path=null!=syncUrl?syncUrl.getPath():null;
                    color=null!=path&&path.length()>=0?R.color.synced:R.color.syncedNone;
                }
            }
        }
        return color;
    }

    public Reply<Path> getSync() {
        return mSync;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getHost());
        dest.writeString(getParent());
        dest.writeString(getName());
        dest.writeString(getExtension());
        dest.writeString(getMime());
        dest.writeString(getMd5());
        dest.writeLong(getAccessTime());
        dest.writeLong(getCreateTime());
        dest.writeLong(getModifyTime());
        dest.writeLong(getLength());
        dest.writeLong(getSize());
        dest.writeInt(getPermissions());
        dest.writeInt(getPort());
    }

    private Path(Parcel parcel){
        if (null!=parcel){
            host=parcel.readString();
            parent=parcel.readString();
            name=parcel.readString();
            extension=parcel.readString();
            mime=parcel.readString();
            md5=parcel.readString();
            accessTime=parcel.readLong();
            createTime=parcel.readLong();
            modifyTime=parcel.readLong();
            length=parcel.readLong();
            size=parcel.readLong();
            permissions=parcel.readInt();
            port=parcel.readInt();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>(){
        @Override
        public Path createFromParcel(Parcel source) {
            return new Path(source);
        }

        @Override
        public Path[] newArray(int size) {
            return new Path[size];
        }
    };

//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public void setAccessTime(long accessTime) {
//        this.accessTime = accessTime;
//    }
//
//    public void setCreateTime(long createTime) {
//        this.createTime = createTime;
//    }
//
//    public void setExtension(String extension) {
//        this.extension = extension;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public void setLength(long length) {
//        this.length = length;
//    }
//
//    public void setMd5(String md5) {
//        this.md5 = md5;
//    }
//
//    public void setMime(String mime) {
//        this.mime = mime;
//    }
//
//    public void setModifyTime(long modifyTime) {
//        this.modifyTime = modifyTime;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setParent(String parent) {
//        this.parent = parent;
//    }
//
//    public void setPermissions(int permissions) {
//        this.permissions = permissions;
//    }
//
//    public void setSize(long size) {
//        this.size = size;
//    }



}
