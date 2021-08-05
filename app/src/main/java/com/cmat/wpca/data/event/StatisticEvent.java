package com.cmat.wpca.data.event;

import com.cmat.wpca.data.StringUtil;
import com.cmat.wpca.data.entry.PlayerEntry;

/**
 * An event that has no effect on the game state, only on stats
 */
public class StatisticEvent extends BaseGameEvent {
    StatisticEventType type;

    public StatisticEvent(PlayerEntry player, StatisticEventType type) {
        this.primaryPlayer = player;
        this.type = type;
    }

    @Override
    public String getEventText() {
        return StringUtil.capitalised(type.toString());
    }

    @Override
    public boolean apply(Game game) {
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return true;
    }

    public enum StatisticEventType {
        BLOCKED,
        MISS,
        BLOCK,
        BADPASS
    }
}
