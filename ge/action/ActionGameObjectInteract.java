package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionGameObjectInteract extends Action{
    @Override
    public String execute() {
        return HandlerInteraction.handleInteractionGameObject(this);
    }
}
