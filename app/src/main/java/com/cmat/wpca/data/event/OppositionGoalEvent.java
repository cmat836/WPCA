package com.cmat.wpca.data.event;

/**
 * Event where the opposition scores a goal
 */
public class OppositionGoalEvent extends BaseGameEvent {
    @Override
    public String getEventText() {
        return "Opposition Goal";
    }

    @Override
    public boolean apply(Game game) {
        game.addOppGoal();
        return true;
    }

    @Override
    public boolean remove(Game game) {
        game.removeOppGoal();
        return true;
    }
}
