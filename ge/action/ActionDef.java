package com.ge.action;

import java.util.HashSet;

public class ActionDef {
    private final String type;
    private final HashSet<String> verbs;
    private final String description;

    public ActionDef(String type, HashSet<String> verbs, String description){
        this.type = type;
        this.verbs = verbs;
        this.description = description;
    }

    public String getType() {
        return type;
    }
    public HashSet<String> getVerbs() {
        return verbs;
    }
    public String getDescription() {
        return description;
    }
}
