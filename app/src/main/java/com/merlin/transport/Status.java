package com.merlin.transport;

public interface Status {
    public final static int IDLE=12312;
    public final static int ADD=12313;
    public final static int PREPARING=12314;
    public final static int PREPARED=12315;
    public final static int STARTED=12317;
    public final static int CONFIRM=12318;
//    public final static int PAUSED=12318;
//    public final static int CANCELED=12319;
    public final static int FINISHED=12320;
    public final static int PROGRESS=12321;
//    public final static int CREATE=12322;
//    public final static int DESTROY=12323;
}
