package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

public abstract class BaseGameEvent implements IGameEvent {
    boolean notable = false;
    long time;

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public boolean isNotable() {
        return notable;
    }

    @Override
    public void setNotable(boolean notable) {
        this.notable = notable;
    }
}
