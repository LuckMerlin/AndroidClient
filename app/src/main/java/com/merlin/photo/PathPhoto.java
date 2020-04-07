package com.merlin.photo;

import android.os.Parcel;
import android.os.Parcelable;

public final class PathPhoto implements Photo{
    private final String mPath;


    public static final Parcelable.Creator<PathPhoto> CREATOR = new Parcelable.Creator<PathPhoto>(){

        @Override
        public PathPhoto createFromParcel(Parcel source) {
            return new PathPhoto(source);
        }

        @Override
        public PathPhoto[] newArray(int size) {
            return new PathPhoto[size];
        }

    };

    public PathPhoto(String path){
        mPath=path;
    }

    private PathPhoto(Parcel parcel){
        mPath=parcel.readString();
    }

    @Override
    public Object getLoadUrl() {
        return mPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mPath);
    }
}
