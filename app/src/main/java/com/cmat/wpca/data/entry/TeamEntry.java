package com.cmat.wpca.data.entry;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class TeamEntry implements IEntry {
    String name;
    String description;
    ArrayList<String> playerNames;

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

    public void addPlayer(String name) {
        playerNames.remove(name);
        playerNames.add(name);
    }

    public void removePlayer(String name) {
        playerNames.remove(name);
    }

    public ArrayList<String> getNameList() {
        return new ArrayList<>(playerNames);
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
