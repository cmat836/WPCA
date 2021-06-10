package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

public class SubEvent extends IGameEvent {
    public PlayerEntry playerOut;
    public PlayerEntry playerIn;

    public SubEvent(PlayerEntry playerIn, PlayerEntry playerOut) {
        this.playerIn = playerIn;
        this.playerOut = playerOut;
    }

    @Override
    public String getEventText() {
        return playerOut.getName() + "->" + playerIn.getName();
    }

    @Override
    public PlayerEntry getPlayer() {
        return playerIn;
    }

}
