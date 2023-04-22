package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionItemDrop extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleItemDrop(this);
    }
}
