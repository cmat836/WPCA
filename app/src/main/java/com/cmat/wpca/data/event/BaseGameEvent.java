package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

import java.util.ArrayList;

/**
 * abstract IGameEvent implementation with implementation for time, notability and player,
 * all implemented get's return default non-null values
 */
public abstract class BaseGameEvent implements IGameEvent {
    boolean notable = false;
    long time = 0L;
    protected PlayerEntry primaryPlayer = PlayerEntry.PlayerBuilder.getBlankPlayer(); // Blank player

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

    @Override
    public PlayerEntry getPlayer() {
        return primaryPlayer;
    }

    @Override
    public ArrayList<PlayerEntry> getPlayers() {
        ArrayList<PlayerEntry> ret = new ArrayList<>();
        ret.add(primaryPlayer);
        return ret;
    }
}
