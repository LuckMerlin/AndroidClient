package com.merlin.task;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class Download  implements Parcelable {
    public final static int TYPE_REPLACE=123;
    public final static int TYPE_NORMAL=124;
    private String mFrom;
    private String mSrc;
    private String mTarget;
    private String mUnique;
    private String mName;
    private int mType=TYPE_REPLACE;

    private Download(Parcel in){
        mSrc= in.readString();
        mTarget = in.readString();
        mUnique=in.readString();
        mName=in.readString();
        mType=in.readInt();
        mFrom=in.readString();
    }

    public Download(String from,String src,String name,String target,String unique){
        mFrom=from;
        mSrc=src;
        mName=name;
        mTarget=target;
        mUnique=unique;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
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

    public String getFrom() {
        return mFrom;
    }

    public int getType() {
        return mType;
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
        dest.writeString(mName);
        dest.writeInt(mType);
        dest.writeString(mFrom);
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof Download){
            Download d=(Download)obj;
            return equal(mFrom,d.mFrom)&&equal(mName,d.mName)&&equal(mSrc,d.mSrc)
                    &&equal(mTarget,d.mTarget);
        }
        return super.equals(obj);
    }

    private boolean equal(String val,String val2){
        if (null==val&&null==val2){
            return true;
        }
        return null!=val&&null!=val2&&val.equals(val2);
    }
}
