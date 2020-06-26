package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * @deprecated
 */
public final class LocalPhoto extends IPath {
    private String mTitle;
    private String mNote;
    private String mMimeType;
    private int mWidth;
    private int mHeight;

    public LocalPhoto(String parent,String name,String extension,String title, String mimeType, int width, int height, String note){
        super(parent,name,extension);
        mTitle=title;
        mMimeType=mimeType;
        mWidth=width;
        mHeight=height;
        mNote=note;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof LocalPhoto){
            String path=((LocalPhoto)obj).getPath(null);
            String currPath=getPath(null);
            return (null==path&&null==currPath)||(null!=path&&null!=currPath&&path.equals(currPath));
        }
        return super.equals(obj);
    }


    private LocalPhoto(Parcel in){
        if (null!=in){
            mTitle=in.readString();
            mNote=in.readString();
            mMimeType=in.readString();
            mWidth=in.readInt();
            mHeight=in.readInt();
            String host=in.readString();
            String parent=in.readString();
            String name=in.readString();
            String extension=in.readString();
            setPath(host,parent,name,extension);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mNote);
        dest.writeString(mMimeType);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeString(getHost());
        dest.writeString(getParent());
        dest.writeString(getName(false));
        dest.writeString(getExtension());
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
