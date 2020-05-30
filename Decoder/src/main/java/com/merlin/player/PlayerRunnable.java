package com.merlin.player;

public abstract class PlayerRunnable implements Runnable {
    private final String mName;

    protected PlayerRunnable(String name) {
        mName=name;
    }

}
