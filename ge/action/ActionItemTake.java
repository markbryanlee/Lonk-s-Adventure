package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionItemTake extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleTakeItem(this);
    }
}
