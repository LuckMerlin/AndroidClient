package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

public final class Love implements Parcelable {
    private long id;
    private String title;
    private String name;
    private Account account;
    private long image;
    private String data;
    private String mode;
    private long createTime;
    private long time;

    public long getTime() {
        return time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public long getImage() {
        return image;
    }

    public String getData() {
        return data;
    }

    public String getMode() {
        return mode;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    private Love(Parcel parcel){
        if (null!=parcel){
            id=parcel.readLong();
            title=parcel.readString();
            name=parcel.readString();
            data=parcel.readString();
            mode=parcel.readString();
            image=parcel.readLong();
            createTime=parcel.readLong();
            time=parcel.readLong();
            account=parcel.readParcelable(Love.class.getClassLoader());
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(data);
        dest.writeString(mode);
        dest.writeLong(image);
        dest.writeLong(createTime);
        dest.writeLong(time);
        dest.writeParcelable(account,flags);
    }


    public static final Parcelable.Creator<Love> CREATOR = new Parcelable.Creator<Love>(){

        @Override
        public Love createFromParcel(Parcel source) {
            return new Love(source);
        }

        @Override
        public Love[] newArray(int size) {
            return new Love[size];
        }

    };

}
