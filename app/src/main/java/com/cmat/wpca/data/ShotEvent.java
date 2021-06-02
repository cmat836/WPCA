package com.cmat.wpca.data;

public class ShotEvent  implements IGameEvent {
    public ShotType type;
    public PlayerEntry player;

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

    @Override
    public long getTime() {
        return 0;
    }

    public enum ShotType {
        GOAL,
        BLOCKED,
        MISS;
    }
}
