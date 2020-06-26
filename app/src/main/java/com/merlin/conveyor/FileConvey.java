package com.merlin.conveyor;

import com.merlin.bean.IPath;

public abstract class FileConvey extends Convey{
    private final IPath mFrom;
    private final IPath mTo;
    private final int mCoverMode;

    public FileConvey(IPath from, IPath to, int coverMode){
        mCoverMode=coverMode;
        mFrom=from;
        mTo=to;
    }

    public final int getCoverMode() {
        return mCoverMode;
    }

    public final IPath getFrom() {
        return mFrom;
    }

    public final IPath getTo() {
        return mTo;
    }

    public static class Progress{
        private long mConveyed;
        private long mTotal;
        private float mSpeed;

        Progress(long total){
            mTotal=total;
        }

        public final long getConveyed() {
            return mConveyed;
        }

        public final long getTotal() {
            return mTotal;
        }

         void setConveyed(long conveyed) {
            this.mConveyed = conveyed;
        }

         void setTotal(long total) {
            this.mTotal = total;
        }

        public final float getProgress() {
            long total=mTotal;
            long conveyed=mConveyed;
            return total<=0?0:(float) (conveyed*100/(double)total);
        }
    }
}
