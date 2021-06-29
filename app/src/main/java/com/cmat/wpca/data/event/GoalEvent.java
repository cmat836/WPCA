package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

/**
 * Event where a friendly player scores a goal
 */
public class GoalEvent extends BaseGameEvent {

    public GoalEvent(PlayerEntry player) {
        this.primaryPlayer = player;
    }

    @Override
    public String getEventText() {
        return "Home Goal";
    }

    @Override
    public boolean apply(Game game) {
        game.addHomeGoal();
        return true;
    }

    @Override
    public boolean remove(Game game) {
        game.removeHomeGoal();
        return true;
    }
}
