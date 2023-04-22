package com.ge.baseobject.gameobject;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.general.World;
import com.ge.util.Constants;

public class StrangeRock extends GameObject{
    private boolean isLooted = false;
    public StrangeRock(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
    }

    @Override
    public String interact(World world) {
        if (!isLooted){
            Entity player = world.getPlayer();
            Item hilt = world.getItemDefs().get(Constants.Items.SWORD_HILT);
            player.addInventoryItem(hilt);
            isLooted = true;
            return world.getGameMessageDefinitions().get(25).getMessage();
        } else return world.getGameMessageDefinitions().get(26).getMessage();

    }
}
