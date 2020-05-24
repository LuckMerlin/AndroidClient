package com.merlin.task;

public interface State {
    public final static int INVALID=0;
    public final static int IDLE=1;
    public final static int PREPARING=2;
    public final static int DOING=3;
    public final static int PAUSE=4;
    public final static int CANCEL=5;
    public final static int RESUME=6;
    public final static int ERROR=7;
    public final static int EXCEPTION=8;
    public final static int FINISH=9;
}
