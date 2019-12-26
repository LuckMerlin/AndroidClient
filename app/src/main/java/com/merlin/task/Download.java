package com.merlin.task;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.debug.Debug;
import com.merlin.util.FileSize;

import java.io.File;

public final class Download  implements Parcelable ,Status {
    public final static int TYPE_REPLACE=123;
    public final static int TYPE_NORMAL=124;
    private String mFromAccount;
    private String mSrc;
    private String mTargetFolder;
    private String mUnique;
    private String mTargetName;
    private int mType=TYPE_NORMAL;
    private boolean mDeleteIncomplete;
    private long mTotal;
    private long mRemain;
    private int mStatus=UNKNOWN;
    private String mMD5;
    private String mFormat;

    private Download(Parcel in){
        mSrc= in.readString();
        mTargetFolder = in.readString();
        mUnique=in.readString();
        mTargetName=in.readString();
        mType=in.readInt();
        mFromAccount=in.readString();
        mDeleteIncomplete=in.readByte()==1;
        mTotal=in.readLong();
        mRemain=in.readLong();
        mStatus=in.readInt();
        mMD5=in.readString();
    }

    public Download(String fromAccount,String src,String name,String targetFolder,String unique){
        mFromAccount=fromAccount;
        mSrc=src;
        mTargetName=name;
        mTargetFolder=targetFolder;
        mUnique=unique;
    }

    public void setDeleteIncomplete(boolean deleteIncomplete) {
        this.mDeleteIncomplete = deleteIncomplete;
    }

    public boolean isDeleteIncomplete() {
        return mDeleteIncomplete;
    }

    public String getTargetName() {
        return mTargetName;
    }

    public void setTargetName(String name) {
        this.mTargetName = name;
    }

    public String getTargetPath(){
        String folder=mTargetFolder;
        String name=mTargetName;
        return (null!=folder?folder:"")+(null!=name?name:"");
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

    public String getFormat() {
        return mFormat;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    protected void setStatus(int status) {
        this.mStatus = status;
    }

    protected void setTotal(long total) {
        this.mTotal = total;
    }

    protected void setRemain(long remain) {
        this.mRemain = remain;
    }

    protected void setFormat(String format) {
        this.mFormat = format;
    }

    protected void setMD5(String md5) {
        this.mMD5 = md5;
    }

    protected void setSrcPath(String src){
        mSrc=src;
    }

    public int getStatus() {
        return mStatus;
    }

    public long getRemain() {
        return mRemain;
    }

    public long getTotal() {
        return mTotal;
    }

    public String getMD5() {
        return mMD5;
    }

    public void setFromAccount(String fromAccount) {
        this.mFromAccount = fromAccount;
    }

    public int getProgressInt(){
        return (int)getProgress();
    }

    public float getProgress(){
        long remain=mRemain;
        long total=mTotal;
        return remain>=0&&remain<=total&&total>0?((total-remain)*100/(float)total):0;
    }

    public boolean buildRemainFromFile(){
        String folder=mTargetFolder;
        String name=mTargetName;
        if (null!=folder&&null!=name&&folder.length()>0&&name.length()>0){
            long total=mTotal;
            long size=new File(folder,name).length();
            if (total>=size){
                mRemain=total-size;
            }
            return false;
        }
        return false;
    }

    public long getDownloadedSize(){
        long remain=mRemain;
        long total=mTotal;
        return remain>=0&&total>=0&&remain<=total?total-remain:0;
    }

    public String getDownloadSizeText(){
        return FileSize.formatSizeText(getDownloadedSize());
    }

    public String getTotalSizeText(){
        long total=mTotal;
        return FileSize.formatSizeText(total>0?total:0);
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
        dest.writeString(mTargetName);
        dest.writeInt(mType);
        dest.writeString(mFromAccount);
        dest.writeByte((byte)(mDeleteIncomplete?1:0));
        dest.writeLong(mTotal);
        dest.writeLong(mRemain);
        dest.writeInt(mStatus);
        dest.writeString(mMD5);
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
                return equal(mFromAccount, d.mFromAccount) && equal(mTargetName, d.mTargetName) && equal(mSrc, d.mSrc)
                        && equal(mTargetFolder, d.mTargetFolder);
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
