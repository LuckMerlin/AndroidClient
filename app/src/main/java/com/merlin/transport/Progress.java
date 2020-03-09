package com.merlin.transport;

public final class Progress {
    private Long mTotalSize,mDoneSize;
    private Float mSpeed;

    public void setSpeed(Float speed) {
        this.mSpeed = speed;
    }

    public void setDoneSize(Long doneSize) {
        this.mDoneSize = doneSize;
    }

    public void setTotalSize(Long totalSize) {
        this.mTotalSize = totalSize;
    }

    public Float getSpeed() {
        return mSpeed;
    }

    public Long getDoneSize() {
        return mDoneSize;
    }

    public Long getTotalSize() {
        return mTotalSize;
    }
}
