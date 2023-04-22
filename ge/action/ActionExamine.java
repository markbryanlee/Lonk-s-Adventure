package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionExamine extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleExamine(this);
    }
}
