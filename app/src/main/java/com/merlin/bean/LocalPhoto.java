package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.photo.Photo;

public final class LocalPhoto implements Parcelable, Photo {
    private String mTitle;
    private String mPath;
    private String mNote;
    private String mMimeType;
    private int mWidth;
    private int mHeight;

    public LocalPhoto(String path, String title, String mimeType, int width, int height, String note){
        mTitle=title;
        mMimeType=mimeType;
        mWidth=width;
        mHeight=height;
        mPath=path;
        mNote=note;
    }

    public String getPath() {
        return mPath;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof LocalPhoto){
            String path=((LocalPhoto)obj).mPath;
            String currPath=mPath;
            return (null==path&&null==currPath)||(null!=path&&null!=currPath&&path.equals(currPath));
        }
        return super.equals(obj);
    }


    private LocalPhoto(Parcel in){
        if (null!=in){
            mTitle=in.readString();
            mPath=in.readString();
            mNote=in.readString();
            mMimeType=in.readString();
            mWidth=in.readInt();
            mHeight=in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPath);
        dest.writeString(mNote);
        dest.writeString(mMimeType);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
    }

    @Override
    public Object getLoadUrl() {
        return mPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<LocalPhoto> CREATOR = new Parcelable.Creator<LocalPhoto>(){

        @Override
        public LocalPhoto createFromParcel(Parcel source) {
            return new LocalPhoto(source);
        }

        @Override
        public LocalPhoto[] newArray(int size) {
            return new LocalPhoto[size];
        }

    };
}
