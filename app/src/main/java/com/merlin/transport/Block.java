package com.merlin.transport;

public final class Block {
    private final Object mData;

    public Block(Object data){
        mData=data;
    }

    public Object getData() {
        return mData;
    }
}
