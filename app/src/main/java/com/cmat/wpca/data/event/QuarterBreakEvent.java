package com.cmat.wpca.data.event;

import com.cmat.wpca.data.StringUtil;

public class QuarterBreakEvent extends BaseGameEvent {
    Game.Quarter prevQuarter;

    public QuarterBreakEvent(Game.Quarter prevQuarter) {
        this.prevQuarter = prevQuarter;
    }

    @Override
    public String getEventText() {
        return "End of " + StringUtil.capitalised(prevQuarter.toString());
    }

    @Override
    public boolean apply(Game game) {
        game.pause();
        game.isQuarterBreak = true;
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }
}
