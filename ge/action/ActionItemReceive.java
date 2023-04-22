package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionItemReceive extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleReceiveItem(this);
    }
}
