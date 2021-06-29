package com.cmat.wpca.data.event;

import com.cmat.wpca.data.StringUtil;
import com.cmat.wpca.data.entry.PlayerEntry;

/**
 * Event triggered whenever a player wins or loses a swimoff
 */
public class SwimoffEvent extends BaseGameEvent {
    private Game.Team previousPossession = Game.Team.NEITHER; // What the possession was before this event went off, used for removing it
    private boolean previousPauseState = false;
    private Game.Quarter previousQuarter = Game.Quarter.PREGAME;
    private final SwimoffResult result;

    public SwimoffEvent(PlayerEntry player, SwimoffResult result) {
        this.primaryPlayer = player;
        this.result = result;
    }

    @Override
    public String getEventText() {
        return "Swimoff " + StringUtil.capitalised(result.toString());
    }

    @Override
    public boolean apply(Game game) {
        previousPossession = game.getPossession();
        game.setPossession(result == SwimoffResult.WIN ? Game.Team.HOME : Game.Team.OPPOSITION);
        // Possession always changes, but timer stuff only happens if we are in pause time and this swimoff is starting the quarter
        if (game.isQuarterBreak()) {
            previousPauseState = true;
            previousQuarter = game.getQuarter();
            if (game.getQuarter() == Game.Quarter.PREGAME) { // If the game has not started yet, start the timer
                game.start();
            } else { // Otherwise resume
                game.resume();
            }
            game.advanceQuarter();
            game.isQuarterBreak = false;
        }
        return true;
    }

    @Override
    public boolean remove(Game game) {
        if (game.isMostRecentOfEvent(this)) { // Only revert this event if another swimoff hasn't occurred
            game.setPossession(previousPossession);
            if (previousPauseState) {
                game.setQuarter(previousQuarter);
                game.pause();
                game.doTimeAdjustment(this, true);
            }
        }
        return true;
    }

    public enum SwimoffResult {
        WIN,
        LOSS;
    }
}
