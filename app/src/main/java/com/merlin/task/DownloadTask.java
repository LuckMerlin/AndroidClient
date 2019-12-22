package com.merlin.task;

import com.merlin.util.FileSize;

import java.nio.file.Files;

public class DownloadTask {
    private final Download mDownload;
    private long mStartTime;
    private long mTotal;
    private long mRemain;
    private boolean mDownloading;
    private boolean mDeleteIncomplete;
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

    public void setDeleteIncomplete(boolean deleteIncomplete) {
        this.mDeleteIncomplete = deleteIncomplete;
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
}
