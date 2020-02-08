package com.merlin.player;

public interface OnPlayerStatusUpdate extends Status {
    void onPlayerStatusUpdated(Player player,int status,String note,Playable media,Object data);
}
