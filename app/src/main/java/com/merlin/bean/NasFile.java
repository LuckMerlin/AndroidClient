package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.Nullable;

import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.browser.Permissions;
import com.merlin.player.Playable;

import static com.merlin.api.What.WHAT_NOT_DIRECTORY;

public class NasFile  extends File{
    private long createTime;
    private NasMedia meta;

    protected NasFile(){
        this(null,null,null,null);
    }

    public NasFile(String host,String parent,String name,String extension){
        super(host,parent,name,extension);
    }

    public double getCreateTime() {
        return createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private NasFile(Parcel dest){
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
            boolean accessible=dest.readBoolean();
            String md5=dest.readString();
            String mime=dest.readString();
            boolean favorite=dest.readBoolean();
            long accessTime=dest.readLong();
            createTime=dest.readLong();
            int permission=dest.readInt();
            setFile(title,imageUrl,childCount,length,modifyTime,accessible,md5,mime,favorite,accessTime,permission);
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
        dest.writeBoolean(isAccessible());
        dest.writeString(getMd5());
        dest.writeString(getMime());
        dest.writeBoolean(isFavorite());
        dest.writeLong(getAccessTime());
        dest.writeLong(createTime);
        dest.writeInt(getPermission());
    }

    public static final Creator<NasFile> CREATOR = new Creator<NasFile>() {
        @Override
        public NasFile createFromParcel(Parcel in) {
            return new NasFile(in);
        }

        @Override
        public NasFile[] newArray(int size) {
            return new NasFile[size];
        }
    };
}
