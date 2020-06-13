package com.merlin.player;

public interface Action {
    public final static int  FATAL_ERROR = -2;
    public final static int  IDLE = 0x1f;//-0000 0001 0000
    public final static int  STOP =  0x2f;//0000 0010 0000
    public final static int  PAUSE =  0x4f;//0000 0100 0000
    public final static int  WAITING = 0x8f;//0000 1000 0000
    public final static int  START =  0x10f;//0001 0000 0000
    public final static int  PLAY = 0x200;//0010 0000 0000
    public final static int  CREATE = 0x40f;//0100 0000 0000
    public final static int  DESTROY = 0x80f;//1000 0000 0000
    public final static int  ADD =  0x1000;//0001 0000 0000 0000
    public final static int  REMOVE = 0x200f;//0010 0000 0000 0000
    public final static int  MODE_CHANGE = 0x400f;//0100 0000 0000 0000
    public final static int  SEEK = 0x800f;//1000 0000 0000 0000
    public final static int  PRE = 0x1000f;//0001 0000 0000 0000 0000
    public final static int  NEXT = 0x2000f;//0010 0000 0000 0000 0000
    public final static int  OPEN = 0x4000f;//0100 0000 0000 0000 0000
    public final static int  CLOSE = 0x8000f;//1000 0000 0000 0000 0000
}
