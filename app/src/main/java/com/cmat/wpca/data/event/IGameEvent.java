package com.cmat.wpca.data.event;

import com.cmat.wpca.data.entry.PlayerEntry;

import java.util.ArrayList;

/**
 * An Event that occurs during the game,
 * can return data about the event to be displayed, differing by implementation,
 * contains the logic to apply and remove the effect of the events to a game
 */
public interface IGameEvent {
    /**
     * Get what occurred during the event in string form
     * @return A String representation of the event
     */
    public String getEventText();

    /**
     * Get the primary player associated with the event, use getPlayers if there might be multiple
     * @return The Primary player associated with the event
     */
    public PlayerEntry getPlayer();

    /**
     * Get all the players associated with the event
     * @return An ArrayList \<PlayerEntry\> of all the players associated with this event
     */
    public ArrayList<PlayerEntry> getPlayers();

    /**
     * Set the time associated with the event
     * @param time Time in milliseconds since the beginning of the game (excluding paused time)
     */
    public void setTime(long time);

    /**
     * Get the time in associated with the event
     * @return The time in milliseconds since the beginning of the game (excluding pause time)
     */
    public long getTime();

    /**
     * Get if the event has been marked as notable
     * @return If the event is marked as notable
     */
    public boolean isNotable();

    /**
     * Mark the events notability
     * @param notable If the event is notable or not
     */
    public void setNotable(boolean notable);

    /**
     * Applys the event's effects to the game (i.e. adding a goal, pausing the time)
     * @param game the game to apply the event to
     * @return if the event's application was successful
     */
    public boolean apply(Game game);

    /**
     * Removes the event's effects from the game, the opposite of IGameEvent.apply (i.e. undoing a goal or other event)
     * @param game the game to remove the event from
     * @return if the removal of the event was successful
     */
    public boolean remove(Game game);
}
