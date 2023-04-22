package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionQuit extends Action{
    @Override
    public String execute() {

        return HandlerInteraction.handleQuit();
    }
}
