package com.cmat.wpca.data;

import com.google.gson.Gson;

public class RulesetEntry implements IEntry {
    String name;
    String parentName;

    String rule1;
    boolean rule2;
    int rule3;
    String rule4;

    public RulesetEntry() {
    }

    public String getName() {
        return name;
    }

    public IEntry getNull() {
        return null;
    }

    public IEntry getFromJSON(Gson gson, String json) {
        return gson.fromJson(json, RulesetEntry.class);
    }

    public String getJSON(Gson gson) {
        return gson.toJson(this);
    }

    public static class RulesetEntryBuilder {
        String name;
        String parentName = "";

        String rule1 = "beep";
        boolean rule2 = false;
        int rule3 = 5;
        String rule4 = "boop";

        public RulesetEntryBuilder(String name) {
            this.name = name;
        }

        public RulesetEntryBuilder parent(RulesetEntry parent) {
            this.parentName = parent.getName();
            return this;
        }

        public RulesetEntryBuilder rules(String r1, boolean r2, int r3, String r4) {
            rule1 = r1;
            rule2 = r2;
            rule3 = r3;
            rule4 = r4;
            return this;
        }

        public RulesetEntry build() {
            RulesetEntry r = new RulesetEntry();
            r.name = name;
            r.parentName = parentName;
            r.rule1 = rule1;
            r.rule2 = rule2;
            r.rule3 = rule3;
            r.rule4 = rule4;
            return r;
        }
    }
}
