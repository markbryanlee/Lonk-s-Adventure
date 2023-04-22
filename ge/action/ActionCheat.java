package com.ge.action;

public class ActionCheat extends Action {
    @Override
    public String execute() {
        return "Executed cheat -> " + getDescription();
    }
}
