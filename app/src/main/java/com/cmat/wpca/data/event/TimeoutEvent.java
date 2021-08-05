package com.cmat.wpca.data.event;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.StringUtil;

/**
 * Event triggered at the beginning or end of a timeout, during a timeout the timeout timer counts up but the game time is paused
 */
public class TimeoutEvent extends BaseGameEvent {
    TimeoutState state;
    Consumer<Long> UICallback;

    /**
     * Either starts or ends a timeout
     * @param state whether this event represents the start or end of a timeout
     * @param UICallback the function to refresh the UI while the timeout is running
     */
    public TimeoutEvent(TimeoutState state, Consumer<Long> UICallback) {
        this.state = state;
        this.UICallback = UICallback;
    }

    @Override
    public String getEventText() {
        return "Timeout " + StringUtil.capitalised(state.toString());
    }

    @Override
    public boolean apply(Game game) {
       if (state == TimeoutState.START) {
           game.addTimeoutTask(UICallback);
           game.pause();
           game.isTimeout = true;
       } else {
           game.resume();
           UICallback.accept(0L);
           game.removeTimeoutTask();
           game.isTimeout = false;
       }

       return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }

    public enum TimeoutState {
        START,
        END
    }
}
