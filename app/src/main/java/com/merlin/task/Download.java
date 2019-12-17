package com.merlin.task;

import android.os.Parcel;
import android.os.Parcelable;

public class Download  implements Parcelable {

    private String mSrc;
    private String mTarget;
    private String mUnique;

    private Download(Parcel in){
        mSrc= in.readString();
        mTarget = in.readString();
        mUnique=in.readString();
    }

    public Download(String src,String target,String unique){
        mSrc=src;
        mTarget=target;
        mUnique=unique;
    }

    public String getSrc() {
        return mSrc;
    }

    public String getTarget() {
        return mTarget;
    }

    public String getUnique() {
        return mUnique;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSrc);
        dest.writeString(mTarget);
        dest.writeString(mUnique);
    }

    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>(){

        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }

    };

    @Override
    public String toString() {
        return "Download{" +
                "mSrc='" + mSrc + '\'' +
                ", mTarget='" + mTarget + '\'' +
                ", mUnique='" + mUnique + '\'' +
                '}';
    }
}
