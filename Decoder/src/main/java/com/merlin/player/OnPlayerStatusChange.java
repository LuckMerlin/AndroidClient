package com.merlin.player;

public interface OnPlayerStatusChange {
    void onPlayerStatusChanged(int status, Media playable, Object arg, String debug);
}
