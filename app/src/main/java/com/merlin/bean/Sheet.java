package com.merlin.bean;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;


public final class Sheet implements Parcelable {
    /**
     * name : 我最喜欢的歌单
     * size : 123
     * cover : null
     * id : 1231321
     * create : 2342
     */
    private String account;
    private String title;
    private long size;
    private String url;
    private String id;
    private String note;
    private long createTime;

    public Sheet(String id,String title,long size){
        setId(id);
        setTitle(title);
        setSize(size);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public long getSize() {
        return size;
    }

    /**
     * @deprecated
     */
    public String getImageUrl() {
        return url;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long create) {
        this.createTime = create;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Sheet(){
        this(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Sheet(Parcel in){
        if (null!=in){
            title=in.readString();
            size=in.readLong();
            url=in.readString();
            id=in.readString();
            createTime=in.readLong();
            account=in.readString();
            note=in.readString();
//            data=in.readParcelableList(new ArrayList<>(), NasMedia.class.getClassLoader());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(size);
        dest.writeString(url);
        dest.writeString(id);
        dest.writeLong(createTime);
        dest.writeString(account);
        dest.writeString(note);
//        dest.writeParcelableList(data,flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Sheet> CREATOR = new Parcelable.Creator<Sheet>(){

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Sheet createFromParcel(Parcel source) {
            return new Sheet(source);
        }

        @Override
        public Sheet[] newArray(int size) {
            return new Sheet[size];
        }

    };

}
