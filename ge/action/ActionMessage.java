package com.ge.action;

public class ActionMessage extends Action{
    @Override
    public String execute() {
        return getDescription();
    }
}
