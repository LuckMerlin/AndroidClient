package com.merlin.player;

public interface OnPlayerStatusUpdate {
    void onPlayerStatusUpdated(Player player,int status,String note,Object media,Object data);
}
