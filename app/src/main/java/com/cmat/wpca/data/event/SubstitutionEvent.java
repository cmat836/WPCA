package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

import java.util.Objects;

public class SubstitutionEvent extends BaseGameEvent {
    PlayerEntry playerOut;
    PlayerEntry playerIn;

    public SubstitutionEvent(PlayerEntry playerOut, PlayerEntry playerIn) {
        this.playerOut = playerOut;
        this.playerIn = playerIn;
    }

    @Override
    public String getEventText() {
        return playerOut.getName() + " sub for " + playerIn.getName();
    }

    @Override
    public boolean apply(Game game) {
        Objects.requireNonNull(game.players.get(playerOut), "No Players").setSelected(false);
        Objects.requireNonNull(game.players.get(playerIn), "No Players").setSelected(true);
        game.selectedPlayers.set(game.selectedPlayers.indexOf(game.players.get(playerOut)), game.players.get(playerIn));
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }
}
