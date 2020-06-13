package com.merlin.media;

import com.merlin.debug.Debug;
import com.merlin.player.Media;
import com.merlin.player.Action;

public class Play {

    public boolean playFromClick(PlayerBinder binder, Media media, int clickCount, String debug){
        PlayerBinder player=binder;
        if (null==media||null==player){
            Debug.W(getClass(),"Can't play media while media or player is NULL."+media+" "+player);
            return false;
        }
        switch (clickCount){
            case 1:
                return player.toggle(Action.PLAY,media,debug);
            case 2:
                return player.toggle(Action.PLAY| Action.ADD,media,debug);
            case 3:
                return player.toggle(Action.WAITING,media,debug);
            case 4:
                return player.toggle(Action.WAITING| Action.ADD,media,debug);
        }
        return false;
    }
}
