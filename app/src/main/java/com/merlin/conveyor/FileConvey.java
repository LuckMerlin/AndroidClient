package com.merlin.conveyor;

import com.merlin.bean.Path;
import com.merlin.debug.Debug;

public abstract class FileConvey extends Convey{
    private final Path mFrom;
    private final Path mTo;
    private final int mCoverMode;

    public FileConvey(Path from,Path to,int coverMode){
        mCoverMode=coverMode;
        mFrom=from;
        mTo=to;
    }

    public final int getCoverMode() {
        return mCoverMode;
    }

    public final Path getFrom() {
        return mFrom;
    }

    public final Path getTo() {
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
