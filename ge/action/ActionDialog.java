package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionDialog extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleDialog(this);
    }
}
