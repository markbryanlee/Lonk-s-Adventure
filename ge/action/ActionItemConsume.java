package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionItemConsume extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleConsumeItem(this);
    }
}
