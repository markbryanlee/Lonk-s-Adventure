package com.ge.action;

import com.ge.baseobject.entity.Entity;
import com.ge.handler.HandlerInteraction;

public class ActionAttack extends Action{

    private Entity attacker;
    private Entity defender;

    public void setAttacker(Entity attacker){
        this.attacker = attacker;
    }

    public void setDefender(Entity defender){
        this.defender = defender;
    }

    public Entity getAttacker() {
        return attacker;
    }

    public Entity getDefender() {
        return defender;
    }

    @Override
    public String execute() {
        return HandlerInteraction.handleAttack( this);
    }
}
