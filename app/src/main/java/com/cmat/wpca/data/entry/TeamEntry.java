package com.cmat.wpca.data.entry;

import java.util.ArrayList;

/**
 * Entry representing a team of players, stores names of players in the team
 */
@SuppressWarnings("unused")
public class TeamEntry implements IEntry {
    String name;
    String description;
    ArrayList<String> playerNames;

    /**
     * Creates a new blank entry
     */
    public TeamEntry() {
        name = "blank";
        description = "blank";
        playerNames = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Add a new player to the team
     * @param name name of the player
     */
    public void addPlayer(String name) {
        playerNames.remove(name);
        playerNames.add(name);
    }

    public void removePlayer(String name) {
        playerNames.remove(name);
    }

    /**
     * Get list of player names
     * @return list of player names
     */
    public ArrayList<String> getNameList() {
        return new ArrayList<>(playerNames);
    }

    /**
     * Gets how large the team is
     * @return the size of the team
     */
    public int getSize() {
        return playerNames.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEntry getNull() {
        return new TeamEntry();
    }
}
