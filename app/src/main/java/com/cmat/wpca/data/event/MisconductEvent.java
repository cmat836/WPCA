package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

public class MisconductEvent extends BaseGameEvent {
    public PlayerEntry player;
    public MisconductType type;
    public String note;

    public MisconductEvent(PlayerEntry player, MisconductType type, String note) {
        this.player = player;
        this.type = type;
        this.note = note;
    }

    @Override
    public String getEventText() {
        return type.toString() + " " + note;
    }

    @Override
    public PlayerEntry getPlayer() {
        return player;
    }


    public enum MisconductType {
        BRUTALITY,
        MISCONDUCT;
    }
}
