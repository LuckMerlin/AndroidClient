package com.merlin.task;

import androidx.annotation.Nullable;

import com.merlin.util.FileSize;

import java.nio.file.Files;

public final class DownloadTask implements Status {
    private final Download mDownload;
    private String mTargetPath;
    private long mStartTime;
    private long mTotal;
    private long mRemain;
    private boolean mDownloading;
    private boolean mDeleteIncomplete;
    private int mStatus=UNKNOWN;
    private String mMD5;

    public DownloadTask(Download download){
        mDownload=download;
    }

    public Download getDownload() {
        return mDownload;
    }

    protected void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    protected void setTotal(long total) {
        this.mTotal = total;
    }

    protected void setRemain(long remain) {
        this.mRemain = remain;
    }

    protected void setDownloading(boolean downloading) {
        this.mDownloading = downloading;
    }

    public boolean isDownloading() {
        return mDownloading;
    }

    public void setTargetPath(String path){
        mTargetPath=path;
    }

    public String getTargetPath() {
        return mTargetPath;
    }

    public void setDeleteIncomplete(boolean deleteIncomplete) {
        this.mDeleteIncomplete = deleteIncomplete;
    }

    protected void setStatus(int status){
        mStatus=status;
    }

    public int getStatus() {
        return mStatus;
    }

    public boolean isDeleteIncomplete() {
        return mDeleteIncomplete;
    }

    public long getRemain() {
        return mRemain;
    }

    public long getTotal() {
        return mTotal;
    }

    public String getDownloadSizeText(){
        long total=mTotal;
        long remain=mRemain;
        return FileSize.formatSizeText(remain>=0&&remain<=total?(total-remain):0);
    }

    public String getTotalSizeText(){
        return FileSize.formatSizeText(mTotal);
    }

    public int getProgress(){
        long remain=mRemain;
        long total=mTotal;
        long progress=total-remain;
        if (progress>=0&&total>0){
            return (int)(progress*100/(float)total);
        }
        return 0;
    }

    public long getDownloaded(){
        long remain=mRemain;
        long total=mTotal;
        long progress=total-remain;
        return progress>=0?progress:0;
    }

    public String getMD5() {
        return mMD5;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj){
            if (obj instanceof Download){
                Download download=mDownload;
                return null!=download&&obj.equals(download);
            }
        }
        return super.equals(obj);
    }
}
