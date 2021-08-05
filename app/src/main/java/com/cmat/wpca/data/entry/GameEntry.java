package com.cmat.wpca.data.entry;

import com.cmat.wpca.data.TaskTimer;
import com.cmat.wpca.data.event.Game;
import com.cmat.wpca.data.event.IGameEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Entry storing all the persistent data of a game, either created from JSON or from a GameBuilder
 */
public class GameEntry implements IEntry {
    private String name = "blank";

    private int homeGoals = 0;
    private int oppGoals = 0;
    private int homePoolPlayers = 7; // Number of players in the pool currently
    private int oppPoolPlayers = 7;

    private Game.Team possession = Game.Team.NEITHER;
    private Game.Quarter quarter = Game.Quarter.PREGAME;

    boolean isTimeout = false;
    boolean isQuarterBreak = true;

    TaskTimer.TaskTimerState gameState;
    TaskTimer.TaskTimerState timeoutState;

    ArrayList<Game.Player> players = new ArrayList<>();
    ArrayList<Game.Player> selectedPlayers = new ArrayList<>();
    ArrayList<IGameEvent> eventRegistry = new ArrayList<>();

    public GameEntry() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEntry getNull() {
        return new GameEntry();
    }

    public static class GameEntryBuilder {
        GameEntry entry = new GameEntry();

        public GameEntryBuilder(String name, int homeGoals, int oppGoals, int homePoolPlayers, int oppPoolPlayers) {
            entry.name = name;
            entry.homeGoals = homeGoals;
            entry.oppGoals = oppGoals;
            entry.homePoolPlayers = homePoolPlayers;
            entry.oppPoolPlayers = oppPoolPlayers;
        }

        public GameEntryBuilder possession(Game.Team possession) {
            entry.possession = possession;
            return this;
        }

        public GameEntryBuilder state(boolean isTimeout, boolean isQuarterBreak, Game.Quarter quarter, TaskTimer.TaskTimerState gameState, TaskTimer.TaskTimerState timeoutState) {
            entry.isTimeout = isTimeout;
            entry.isQuarterBreak = isQuarterBreak;
            entry.quarter = quarter;
            entry.gameState = gameState;
            entry.timeoutState = timeoutState;
            return this;
        }

        public GameEntryBuilder players(HashMap<PlayerEntry, Game.Player> players, ArrayList<Game.Player> selectedPlayers) {
            entry.players.addAll(players.values());
            entry.selectedPlayers = selectedPlayers;
            return this;
        }

        public GameEntryBuilder events(ArrayList<IGameEvent> eventRegistry) {
            entry.eventRegistry = eventRegistry;
            return this;
        }

        public GameEntry build() {
            return entry;
        }
    }
}
