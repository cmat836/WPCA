package com.cmat.wpca.data.event;

import com.cmat.wpca.data.StringUtil;
import com.cmat.wpca.data.entry.PlayerEntry;

/**
 * Event to change the possession from one team to the other
 */
public class PossessionChangeEvent extends BaseGameEvent {
    PossessionChangeType type;

    /**
     *
     * @param player The player that either took or lost the ball, if the turnover did not
     *               involve a player pass null and the event will return a blank player
     * @param type The type of turnover, steal gives the ball to the home team, turnover gives it to the opposition
     */
    public PossessionChangeEvent(PlayerEntry player, PossessionChangeType type) {
        this.type = type;
        if (player != null) {
            this.primaryPlayer = player;
        }
    }

    @Override
    public String getEventText() {
        return StringUtil.capitalised(type.toString());
    }

    @Override
    public boolean apply(Game game) {
        if (type == PossessionChangeType.STEAL) {
            game.setPossession(Game.Team.HOME);
        } else {
            game.setPossession(Game.Team.OPPOSITION);
        }
        return true;
    }

    @Override
    public boolean remove(Game game) {
        if (game.isMostRecentOfEvent(this)) { // Only revert the event if its the most recent
            if (type == PossessionChangeType.STEAL) {
                game.setPossession(Game.Team.OPPOSITION);
            } else {
                game.setPossession(Game.Team.HOME);
            }
        }
        return true;
    }

    public enum PossessionChangeType {
        STEAL,
        TURNOVER;
    }
}
