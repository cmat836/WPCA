package com.cmat.wpca.data;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class PlayerEntry implements IEntry {
    String name;

    String birthday;
    Handedness handedness;
    boolean isGoalie;
    String number;

    ArrayList<String> teams;

    public PlayerEntry() {
        name = "blank";
        birthday = "0/0/0";
        handedness = Handedness.RIGHT;
        isGoalie = false;
        number = "0";
        teams = new ArrayList<>();
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Handedness getHandedness() {
        return handedness;
    }

    public void setHandedness(Handedness handedness) {
        this.handedness = handedness;
    }

    public boolean isGoalie() {
        return isGoalie;
    }

    public void setGoalie(boolean goalie) {
        isGoalie = goalie;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTeam(String name) {
        teams.remove(name);
        teams.add(name);
    }

    public void setTeams(ArrayList<String> team) {
        teams = team;
    }

    public void removeTeam(String name) {
        teams.remove(name);
    }

    public ArrayList<String> getTeamList() {
        ArrayList<String> r = new ArrayList<>();
        r.addAll(teams);
        return r;
    }

    public boolean hasTeam() {
        return !teams.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEntry getNull() {
        return new PlayerEntry();
    }

    public enum Handedness {
        LEFT,
        RIGHT
    }

    public static class PlayerBuilder {
        String name;
        String birthday;
        Handedness handedness = Handedness.RIGHT;
        boolean isGoalie = false;
        String number = "0";

        public PlayerBuilder(String name, String number) {
            this.name = name;
            this.number = number;
            birthday = "0/0/0";
        }

        public PlayerBuilder(String name, String number, String birthday) {
            this.name = name;
            this.number = number;
            this.birthday = birthday;
        }

        public PlayerBuilder birthday(Date birthday) {
            this.birthday = birthday.toString();
            return this;
        }

        public PlayerBuilder handedness(Handedness handedness) {
            this.handedness = handedness;
            return this;
        }

        public PlayerBuilder setGoalie(boolean goalie) {
            this.isGoalie = goalie;
            return this;
        }

        public PlayerEntry build() {
            PlayerEntry built = new PlayerEntry();
            built.name = name;
            built.birthday = birthday;
            built.handedness = handedness;
            built.isGoalie = isGoalie;
            built.number = number;
            return built;
        }
    }
}
