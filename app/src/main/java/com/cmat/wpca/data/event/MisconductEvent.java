package com.cmat.wpca.data.event;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.StringUtil;
import com.cmat.wpca.data.entry.PlayerEntry;

public class MisconductEvent extends BaseGameEvent {
    MisconductType type;
    String note;
    Consumer<Long> UIUpdateCallBack;
    Consumer<Long> UIEndCallBack;

    public MisconductEvent(PlayerEntry player, MisconductType type, String note, Consumer<Long> UIUpdateCallBack, Consumer<Long> UIEndCallBack) {
        this.primaryPlayer = player;
        this.type = type;
        this.note = note;
        this.UIUpdateCallBack = UIUpdateCallBack;
        this.UIEndCallBack = UIEndCallBack;
    }

    @Override
    public String getEventText() {
        return StringUtil.capitalised(type.toString()) + " : " + note;
    }

    @Override
    public boolean apply(Game game) {
        if (type == MisconductType.MISCONDUCT) {
            game.excludePlayer(primaryPlayer, 20000);
            game.addExclusionTask("misconduct" + primaryPlayer.getName(), UIUpdateCallBack, 20000, UIEndCallBack);
        } else {
            game.excludePlayer(primaryPlayer, 240000);
            game.addExclusionTask("misconduct" + primaryPlayer.getName(), UIUpdateCallBack, 240000, UIEndCallBack);
        }
        return true;
    }

    @Override
    public boolean remove(Game game) {
        return false;
    }

    public enum MisconductType {
        BRUTALITY,
        MISCONDUCT
    }

    public void end(Game game) {
        game.removeExclusionTask("misconduct" + primaryPlayer.getName());
        game.endPlayerExclusion(primaryPlayer);
    }
}
