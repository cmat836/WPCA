package com.cmat.wpca.data;

public interface IGameEvent {
    public String getEventText();
    public PlayerEntry getPlayer();
    public long getTime();
}
