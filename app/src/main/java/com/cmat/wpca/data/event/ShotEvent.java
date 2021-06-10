package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

public class ShotEvent  extends IGameEvent {
    public ShotType type;
    public PlayerEntry player;
    public boolean isPenalty = false;

    public ShotEvent(ShotType type, PlayerEntry player) {
        this.type = type;
        this.player = player;
    }

    @Override
    public String getEventText() {
        return type.toString();
    }

    @Override
    public PlayerEntry getPlayer() {
        return player;
    }

    public enum ShotType {
        GOAL,
        BLOCKED,
        MISS;
    }
}
