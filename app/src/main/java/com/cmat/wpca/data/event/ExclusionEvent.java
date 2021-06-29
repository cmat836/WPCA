package com.cmat.wpca.data.event;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.entry.PlayerEntry;

/**
 * Event triggered when a player is excluded
 */
public class ExclusionEvent extends BaseGameEvent {
    Consumer<Long> UIUpdateCallBack;
    Consumer<Long> UIEndCallBack;

    public ExclusionEvent(PlayerEntry player, Consumer<Long> UIUpdateCallBack, Consumer<Long> UIEndCallBack) {
        this.primaryPlayer = player;
        this.UIUpdateCallBack = UIUpdateCallBack;
        this.UIEndCallBack = UIEndCallBack;
    }

    @Override
    public String getEventText() {
        return "Exclusion";
    }

    @Override
    public boolean apply(Game game) {
        game.excludePlayer(primaryPlayer, 20000);
        game.addExclusionTask("exclusion" + primaryPlayer.getName(), UIUpdateCallBack, 20000, UIEndCallBack);
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }

    public void end(Game game) {
        game.removeExclusionTask("exclusion" + primaryPlayer.getName());
        game.endPlayerExclusion(primaryPlayer);
    }
}
