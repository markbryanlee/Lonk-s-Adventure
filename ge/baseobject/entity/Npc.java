package com.ge.baseobject.entity;

import com.ge.general.ApplicationWindow;
import com.ge.general.World;

public class Npc extends Entity implements java.io.Serializable{

    @Override
    public void death() {
        getRoom().killNpc(this);
    }

    public Npc(int id, String name, String examine, boolean isAttackable) {
        super(id, name, examine, isAttackable);
    }

    @Override
    public String interact(World world) {
        return "Nothing interesting happens...";
    }

}
