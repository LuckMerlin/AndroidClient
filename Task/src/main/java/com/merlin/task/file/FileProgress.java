package com.merlin.task.file;

import com.merlin.task.Progress;

import java.math.BigDecimal;

public class FileProgress implements Progress {
    private long mPerBytes;
    private long mDone;
    private final long mTotal;

    public FileProgress(long done,long total){
        mDone=done;
        mTotal=total;
    }

    @Override
    public final String getText(int progressWhat) {
        switch (progressWhat){
            case DONE:
                return formatSizeText(mDone);
            case TOTAL:
                return formatSizeText(mTotal);
            case SPEED:
                String speed=formatSizeText(mPerBytes);
                return null!=speed?speed+"/s":null;
            case PROGRESS:
                return ""+getProgress();
        }
        return null;
    }

    @Override
    public final float getProgress() {
        long done=mDone;
        long total=mTotal;
        return total>0?(done*100.0f)/total:0;
    }

    public final boolean setDone(long done) {
        if (done>=0&&mDone!=done) {
            mDone = done;
            return true;
        }
        return false;
    }

    public final boolean setPerBytes(long perBytes) {
        if (perBytes!=mPerBytes){
            mPerBytes = perBytes;
            return true;
        }
        return false;
    }

    public final long getPerBytes() {
        return mPerBytes;
    }

    public final long getDone() {
        return mDone;
    }

    public final long getTotal() {
        return mTotal;
    }

    private String formatSizeText(double fileSize){
        double kiloByte = fileSize/1024;
        if(kiloByte < 1) {
            return fileSize + "B";
        }
        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }
        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }

    @Override
    public String toString() {
        return  " done="+mDone+" total"+mTotal+" speed="+mPerBytes+" "+super.toString();
    }
}
