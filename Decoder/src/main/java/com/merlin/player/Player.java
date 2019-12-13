package com.merlin.player;

public class Player {

    static {
        System.loadLibrary("linqiang");
    }

    public native boolean play(String path,float seek);
}
