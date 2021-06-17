package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

public class NonPlayerEvent extends BaseGameEvent {
    NonPlayerEventType type;

    public NonPlayerEvent(NonPlayerEventType type) {
        this.type = type;
    }

    @Override
    public String getEventText() {
        return type.toString();
    }

    @Override
    public PlayerEntry getPlayer() {
        return (PlayerEntry) new PlayerEntry().getNull();
    }

    public enum NonPlayerEventType {
        OPPGOAL,
        UNEVENOPPSTART,
        UNEVENOPPEND,
        TIMEOUTSTART,
        TIMEOUTEND,
        POSSESSIONCHANGEHOME,
        POSSESSIONCHANGEOPP;
    }
}
