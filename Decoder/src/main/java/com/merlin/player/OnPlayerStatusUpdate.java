package com.merlin.player;

/**
 * @deprecated
 */
public interface OnPlayerStatusUpdate extends Status {
    void onPlayerStatusUpdated(BK_Player player, int status, String note, IPlayable media, Object data);
}
