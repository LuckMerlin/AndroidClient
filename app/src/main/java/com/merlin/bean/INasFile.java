package com.merlin.bean;

import android.os.Parcel;

public class INasFile extends Document {
    private long createTime;
    private INasMedia meta;

    protected INasFile(){
        this(null,null,null,null);
    }

    public INasFile(String host, String parent, String name, String extension){
        super(host,parent,name,extension);
    }

    public double getCreateTime() {
        return createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private INasFile(Parcel dest){
        if (null!=dest){
            String host=dest.readString();
            String parent=dest.readString();
            String name=dest.readString();
            String extension=dest.readString();
            setPath(host,parent,name,extension);
            String title=dest.readString();
            String imageUrl=dest.readString();
            int childCount=dest.readInt();
            long length=dest.readLong();
            long modifyTime=dest.readLong();
            String md5=dest.readString();
            String mime=dest.readString();
            boolean favorite=dest.readBoolean();
            long accessTime=dest.readLong();
            createTime=dest.readLong();
            int permission=dest.readInt();
            setFile(title,imageUrl,childCount,length,modifyTime,md5,mime,favorite,accessTime,permission);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getHost());
        dest.writeString(getParent());
        dest.writeString(getName());
        dest.writeString(getExtension());
        dest.writeString(getTitle());
        dest.writeString(getImageUrl());
        dest.writeInt(getChildCount());
        dest.writeLong(getLength());
        dest.writeLong(getModifyTime());
        dest.writeString(getMd5());
        dest.writeString(getMime());
        dest.writeBoolean(isFavorite());
        dest.writeLong(getAccessTime());
        dest.writeLong(createTime);
        dest.writeInt(getPermission());
    }

    public final INasMedia getMeta() {
        return meta;
    }

    public static final Creator<INasFile> CREATOR = new Creator<INasFile>() {
        @Override
        public INasFile createFromParcel(Parcel in) {
            return new INasFile(in);
        }

        @Override
        public INasFile[] newArray(int size) {
            return new INasFile[size];
        }
    };
}
