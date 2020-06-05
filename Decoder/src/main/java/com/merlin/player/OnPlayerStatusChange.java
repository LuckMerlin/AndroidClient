package com.merlin.player;

public interface OnPlayerStatusChange {
    void onPlayerStatusChanged(int status,Playable playable,Object arg,String debug);
}
