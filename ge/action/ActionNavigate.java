package com.ge.action;

import com.ge.handler.HandlerInteraction;

public class ActionNavigate extends Action   implements java.io.Serializable{
    @Override
    public String execute() {
        return HandlerInteraction.handleRoomChange(this);
    }
}
