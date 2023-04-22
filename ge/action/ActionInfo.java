package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionInfo extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleInfo(this);
    }
}
