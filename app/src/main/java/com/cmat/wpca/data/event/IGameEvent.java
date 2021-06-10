package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

public abstract class IGameEvent {
    boolean notable = false;
    long time;

    public abstract String getEventText();
    public abstract PlayerEntry getPlayer();

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public boolean isNotable() {
        return notable;
    }

    public void setNotable(boolean notable) {
        this.notable = notable;
    }
}
