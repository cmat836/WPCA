package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

public class GenericPlayerEvent extends BaseGameEvent {
    public GenericEventType type;
    public PlayerEntry player;

    public GenericPlayerEvent(GenericEventType type, PlayerEntry player) {
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


    public enum GenericEventType {
        TURNOVER,
        EXCLUSION,
        BLOCK,
        BADPASS;
    }
}
