package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

public class SwimoffEvent extends BaseGameEvent {
    public PlayerEntry player;
    public SwimoffResult outcome;

    public SwimoffEvent(PlayerEntry player, SwimoffResult outcome) {
        this.player = player;
        this.outcome = outcome;
    }

    @Override
    public String getEventText() {
        return "Swimoff " + outcome.toString();
    }

    @Override
    public PlayerEntry getPlayer() {
        return player;
    }


    public enum SwimoffResult {
        WIN,
        LOSE;
    }
}
