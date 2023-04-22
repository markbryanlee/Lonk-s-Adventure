package com.ge.action;

public class ActionUnrecognized extends Action{
    @Override
    public String execute() {
        return "Did not recognize the command.";
    }
}
