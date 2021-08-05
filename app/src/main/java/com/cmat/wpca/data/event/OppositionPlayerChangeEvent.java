package com.cmat.wpca.data.event;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.StringUtil;

/**
 * Event triggered when the opposition loses or gains a player
 */
public class OppositionPlayerChangeEvent extends BaseGameEvent {
    ChangeType type;
    Consumer<Long> UICallback;

    public OppositionPlayerChangeEvent(ChangeType type, Consumer<Long> UICallback) {
        this.type = type;
        this.UICallback = UICallback;
    }

    @Override
    public String getEventText() {
        return "Opposition " + StringUtil.capitalised(type.toString()) + " Player";
    }

    @Override
    public boolean apply(Game game) {
        if (type == ChangeType.LOST) {
            game.setUneven(6);
            game.addGameTask("uneven", UICallback);
        } else {
            game.setUneven(7);
            UICallback.accept(0L);
            game.removeGameTask("uneven");
        }
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }

    public enum ChangeType {
        GAINED,
        LOST
    }
}
