package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionInventory extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleInventory(this);
    }
}
