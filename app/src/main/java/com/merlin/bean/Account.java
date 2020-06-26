package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

public final class Account implements Parcelable {
    private long id;
    private String name;
    private String account;
    private String permissions;
    private String path;

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPath() {
        return path;
    }

    public String getPermissions() {
        return permissions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Account(Parcel parcel){
        if (null!=parcel){
            id=parcel.readLong();
            name=parcel.readString();
            account=parcel.readString();
            permissions=parcel.readString();
            path=parcel.readString();
        }
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(account);
        dest.writeString(permissions);
        dest.writeString(path);
    }


    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>(){

        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }

    };

}
