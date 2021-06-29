package com.cmat.wpca.data.event;

import androidx.core.util.Consumer;

import com.cmat.wpca.data.TaskTimer;
import com.cmat.wpca.data.entry.PlayerEntry;
import com.cmat.wpca.data.event.IGameEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A structure for storing all the data associated with a single game of waterpolo,
 * stores the game state and game event history,
 * his is the structure that is stored into database or file after the game for evaluation and sharing. <br><br>
 * Should ideally only contain data and very little program logic (besides getters and setters with a bit of verification),
 * this structure should only be interacted with via IGameEvents passed to it <br><br>
 * The game starts paused in the PREGAME state, calling Game.start() will put it into Q1 and start the game timer
 */
public class Game {
    int homeGoals = 0;
    int oppGoals = 0;
    int homePoolPlayers = 7; // Number of players in the pool currently
    int oppPoolPlayers = 7;

    Team possession = Team.NEITHER;
    Quarter quarter = Quarter.PREGAME;

    final TaskTimer gameTime = new TaskTimer(100); // Timer of how much playtime has elapsed since the game started
    final TaskTimer timeoutTime = new TaskTimer(100); // How much time has elapsed during the current pause

    boolean isTimeout = false;
    boolean isQuarterBreak = true;

    HashMap<PlayerEntry, Player> players = new HashMap<>();
    ArrayList<Player> selectedPlayers = new ArrayList<>();
    ArrayList<IGameEvent> eventRegistry = new ArrayList<>();

    /**
     * Creates a new game instance with the passed players
     * @param players The players who will be taking part in the game
     * @param selectedPlayers The players who will start in the pool
     */
    public Game(ArrayList<PlayerEntry> players, ArrayList<PlayerEntry> selectedPlayers) {
        for (PlayerEntry p : players) {
            Player np = new Player(p);
            np.setSelected(selectedPlayers.contains(p));
            this.players.put(p, np);
            this.selectedPlayers.add(np);
        }
    }

    /**
     * Attempts to apply the passed event to the game, if successful adds it to the games event register,
     * if false is returned no change was made to the game
     * @param event The event to apply to the game
     * @return if the application of the event was successful
     */
    public boolean addEvent(IGameEvent event) {
        if (event.apply(this)) {
            event.setTime(gameTime.getMillis());
            eventRegistry.add(event);
            return true;
        }
        return false;
    }

    /**
     * Attempts to remove the effect of a previously applied event from the game, attempting to undo any follow on effects.
     * The event passed to this should be obtained from the game using one of the getEvent methods
     * @param event The event to remove from the game (from Game.getEvent)
     * @return if the removal was successful
     */
    public boolean removeEvent(IGameEvent event) {
        if (event.remove(this)) {
            eventRegistry.remove(event);
            return true;
        }
        return false;
    }

    /**
     * Get an event from the event registry and the specified index
     * @param index the index in the eventRegistry, if the index is too large will return the final event
     * @return the event
     */
    public IGameEvent getEvent(int index) {
        return eventRegistry.get(index >= eventRegistry.size() ? eventRegistry.size() - 1 : index);
    }

    /**
     * Returns the most recently added event
     * @return The event
     */
    public IGameEvent getEvent() {
        return eventRegistry.get(eventRegistry.size() - 1);
    }

    /**
     * Gets the number of events in the registry
     * @return the number of events
     */
    public int getEventCount() {
        return eventRegistry.size();
    }

    /**
     * Returns if the events passed is the most recent of its type, this is for checking if another event
     * has already overwritten its effect for the purpose of removing events
     * @param event
     * @return
     */
    public boolean isMostRecentOfEvent(IGameEvent event) {
        Class<? extends IGameEvent> c = event.getClass();
        int index = eventRegistry.indexOf(event);

        for (int i = index + 1; i < eventRegistry.size(); i++) { // Search the registry from the next event onwards
            if (eventRegistry.get(i).getClass().equals(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs an adjustment to the games timer when a time pausing/resuming event is removed.
     * Adds/Removes the time lost/gained from the current game time and all subsequent events<br /><br />
     * e.g. A swimoff is accidentally applied and time starts then 2 events are added, the swimoff is removed after 10 seconds,
     * this will return the game time to what it was before the swimoff and remove the time from the 2 events
     * @param event the time adjusting event that is being removed
     * @param removeTime true if time is to be removed, false if it is to be added
     */
    protected void doTimeAdjustment(IGameEvent event, boolean removeTime) {
        long currentTime = gameTime.getMillis();
        long diff = (currentTime - event.getTime()) * (removeTime ? -1 : 1);
        int index = eventRegistry.indexOf(event);

        gameTime.adjust(diff);

        for (int i = index + 1; i < eventRegistry.size(); i++) {
            eventRegistry.get(i).setTime(eventRegistry.get(i).getTime() + diff);
        }
    }

    /**
     * Starts the games timer
     */
    public void start() {
        gameTime.start();
    }

    /**
     * Pauses the games timer and starts the timeout timer
     */
    public void pause() {
        gameTime.pause();
        timeoutTime.start();
    }

    /**
     * Resumes the games timer
     */
    public void resume() {
        gameTime.resume();
        timeoutTime.stop();
    }

    /**
     * Move the game to the next quarter
     */
    public void advanceQuarter() {
        quarter = quarter.next();
    }

    /*************************************************
     * Getters
     **********************************************/

    public Team getPossession() {
        return possession;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getOppGoals() {
        return oppGoals;
    }

    public Quarter getQuarter() { return quarter; }

    public boolean isQuarterBreak() {
        return isQuarterBreak;
    }

    public boolean isTimeout() {
        return isTimeout;
    }

    public boolean isUneven() {
        return oppPoolPlayers != 7;
    }

    /**
     * Get all the (home) players in the game
     * @return a list of all players in the game
     */
    public ArrayList<PlayerEntry> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    /**
     * Gets all the players currently selected (in the pool/in the pool but excluded)
     * @return a list of all currently selected players
     */
    public ArrayList<PlayerEntry> getSelectedPlayers() {
        ArrayList<PlayerEntry> ret = new ArrayList<>();
        for (Player p : selectedPlayers) {
            if (p.isSelected()) {
                ret.add(p.getPlayer());
            }
        }
        return ret;
    }

    /**
     * Gets the first goalie in the team
     * @return the goalie
     */
    public PlayerEntry getGoalie() {
        for (Player p : players.values()) {
            if (p.getPlayer().isGoalie()) {
                return p.getPlayer();
            }
        }
        return (PlayerEntry) new PlayerEntry().getNull();
    }

    /********************************************
     * Setters
     ******************************************/

    protected void addHomeGoal() {
        homeGoals++;
    }

    protected void addOppGoal() {
        oppGoals++;
    }

    protected void removeHomeGoal() {
        homeGoals = Math.max(homeGoals--, 0);
    }

    protected void removeOppGoal() {
        oppGoals = Math.max(oppGoals--, 0);
    }

    protected void setPossession(Team team) {
        this.possession = team;
    }

    protected void setQuarter(Quarter quarter) {
        this.quarter = quarter;
    }

    protected void setUneven(int oppPoolPlayers) {
        this.oppPoolPlayers = oppPoolPlayers;
    }

    /**
     * Adds a new indefinite task to timeout timer
     * @param task
     */
    protected void addTimeoutTask(Consumer<Long> task) {
        timeoutTime.addTask("timeout", true, task, 200L);
    }

    /**
     * Removes the timeout tasks
     */
    protected void removeTimeoutTask() {
        timeoutTime.removeTask("timeout");
    }

    /**
     * Adds a new indefinite task to the game timer
     * @param taskName
     * @param task
     */
    protected void addGameTask(String taskName, Consumer<Long> task) {
        gameTime.addTask(taskName, true, task, 200L);
    }

    protected void removeGameTask(String taskName) {
        gameTime.removeTask(taskName);
    }

    protected void addExclusionTask(String taskName, Consumer<Long> task, long duration, Consumer<Long> endTask) {
        gameTime.addTask(taskName, true, task, 200L, duration, endTask);
    }

    protected void removeExclusionTask(String taskName) {
        gameTime.removeTask(taskName);
    }

    /**
     * Selects the player to be either selected or not
     */
    public void setPlayerSelected(PlayerEntry player, boolean selected) {
        players.get(player).setSelected(selected);
    }

    public void excludePlayer(PlayerEntry player, int duration) {
        Player p = players.get(player);
        p.setExclusions(p.getExclusions() + 1);
        p.setExcluded(true);
        p.setExclusionTimeRemaining(20000);
        this.homePoolPlayers--;
    }

    public void endPlayerExclusion(PlayerEntry player) {
        Player p = players.get(player);
        p.setExcluded(false);
        p.setExclusionTimeRemaining(0);
        this.homePoolPlayers++;
    }

    /**
     * Container for a player in the game, stores information about exclusions, substitutions and other transient data
     */
    class Player {
        PlayerEntry player = (PlayerEntry) new PlayerEntry().getNull();

        int exclusions = 0;
        boolean selected = false; // Is the player currently playing
        boolean excluded = false;
        long exclusionTimeRemaining = 0L;

        public Player(PlayerEntry player) {
            this.player = player;
        }

        public PlayerEntry getPlayer() {
            return player;
        }

        public boolean isExcluded() {
            return excluded;
        }

        public void setExcluded(boolean excluded) {
            this.excluded = excluded;
        }

        public int getExclusions() {
            return exclusions;
        }

        public void setExclusions(int exclusions) {
            this.exclusions = exclusions;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public long getExclusionTimeRemaining() {
            return exclusionTimeRemaining;
        }

        public void setExclusionTimeRemaining(long exclusionTimeRemaining) {
            this.exclusionTimeRemaining = exclusionTimeRemaining;
        }

    }

    public enum Team {
        NEITHER,
        HOME,
        OPPOSITION;
    }

    /**
     * Stores which state the game is at currently and has the functionality to move to the next
     */
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
