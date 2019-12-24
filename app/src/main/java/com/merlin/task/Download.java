package com.merlin.task;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class Download  implements Parcelable {
    public final static int TYPE_REPLACE=123;
    public final static int TYPE_NORMAL=124;
    private String mFromAccount;
    private String mSrc;
    private String mTargetFolder;
    private String mUnique;
    private String mName;
    private int mType=TYPE_NORMAL;
    private boolean mDeleteIncomplete;

    private Download(Parcel in){
        mSrc= in.readString();
        mTargetFolder = in.readString();
        mUnique=in.readString();
        mName=in.readString();
        mType=in.readInt();
        mFromAccount=in.readString();
        mDeleteIncomplete=in.readBoolean();
    }

    public Download(String fromAccount,String src,String name,String targetFolder,String unique){
        mFromAccount=fromAccount;
        mSrc=src;
        mName=name;
        mTargetFolder=targetFolder;
        mUnique=unique;
    }


    public void setDeleteIncomplete(boolean deleteIncomplete) {
        this.mDeleteIncomplete = deleteIncomplete;
    }

    public boolean isDeleteIncomplete() {
        return mDeleteIncomplete;
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

    public String getTargetFolder() {
        return mTargetFolder;
    }

    public String getUnique() {
        return mUnique;
    }

    public String getFromAccount() {
        return mFromAccount;
    }

    public int getType() {
        return mType;
    }



    public void setType(int type) {
        this.mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSrc);
        dest.writeString(mTargetFolder);
        dest.writeString(mUnique);
        dest.writeString(mName);
        dest.writeInt(mType);
        dest.writeString(mFromAccount);
        dest.writeBoolean(mDeleteIncomplete);
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
                ", mTarget='" + mTargetFolder + '\'' +
                ", mUnique='" + mUnique + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj) {
            if (obj instanceof Download) {
                Download d = (Download) obj;
                return equal(mFromAccount, d.mFromAccount) && equal(mName, d.mName) && equal(mSrc, d.mSrc)
                        && equal(mTargetFolder, d.mTargetFolder);
            }else if (obj instanceof DownloadTask){
                return equals(((DownloadTask)obj).getDownload());
            }
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
