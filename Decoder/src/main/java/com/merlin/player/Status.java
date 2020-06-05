package com.merlin.player;

public interface Status {
    public final static int  FATAL_ERROR = -2;
    public final static int  IDLE = -2003;
    public final static int  STOP =  -2005;
    public final static int  PAUSE =  -2006;
    public final static int  PLAYING =  -2007;
    public final static int  WAITING =  -2008;
    public final static int  START =  -2009;
    public final static int  CREATE =  -2021;
    public final static int  DESTROY =  -2022;
    public final static int  ADD =  -2023;
    public final static int  REMOVE =  -2024;
}
