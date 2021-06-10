package com.cmat.wpca.data.entry;

import java.util.ArrayList;

public class RulesetEntry implements IEntry {
    String name;
    String parentName;

    ArrayList<Rule> rules;

    public RulesetEntry() {
        rules = new ArrayList<>();
        name = "blank";
    }

    public Object getRuleByPosition(int position) {
        if (position == 0) {
            return name;
        } else {
            return rules.get(position - 1);
        }
    }

    public void modifyRule(String name, String rule) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IEntry getNull() {
        return new RulesetEntryBuilder("blank").build();
    }

    public static class Rule {
        String name;
        String info;

        public Rule() {
            name = "";
            info = "";
        }

        public Rule(String name, String info) {
            this.name = name;
            this.info = info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }
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
            r.rules = new ArrayList<>();
            r.rules.add(new Rule("rule1", rule1));
            r.rules.add(new Rule("rule2", String.valueOf(rule2)));
            r.rules.add(new Rule("rule3", String.valueOf(rule3)));
            r.rules.add(new Rule("rule4", rule4));
            return r;
        }
    }
}
