package com.merlin.conveyor;

public abstract class FileConvey extends Convey{

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
    }
}
