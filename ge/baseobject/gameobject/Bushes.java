package com.ge.baseobject.gameobject;

import com.ge.general.World;

public class Bushes extends GameObject{
    public Bushes(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
    }

    @Override
    public String interact(World world) {
        return world.getGameMessageDefinitions().get(16).getMessage();
    }
}
