package com.cmat.wpca.data;

import android.os.Handler;
import android.os.SystemClock;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.event.IGameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Game {
    public int homeGoals = 0;
    public int oppGoals = 0;

    public boolean homePossession = true;
    public boolean unevenHome = false;
    public boolean unevenOpp = false;

    public Quarter quarter = Quarter.PREGAME;


    public boolean timeout = false;
    public boolean quarterbreak = false;

    public boolean paused = true;

    public ArrayList<IGameEvent> GameEvents = new ArrayList<>();

    public void advanceQuarter() {
        quarter = quarter.next();
    }

    public enum Quarter {
        PREGAME,
        Q1,
        Q2,
        Q3,
        Q4;

        public Quarter next() {
            return Quarter.values()[this.ordinal() + 1];
        }
    }
}
