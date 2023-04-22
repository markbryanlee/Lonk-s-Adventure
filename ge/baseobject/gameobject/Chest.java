package com.ge.baseobject.gameobject;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.general.World;
import com.ge.util.Constants;

public class Chest extends GameObject {

    private boolean isLooted = false;
    public Chest(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
    }

    @Override
    public String interact(World world) {

        if (!isLooted){
            Entity player = world.getPlayer();
            Item dingedSword = world.getItemDefs().get(Constants.Items.DINGED_SWORD);
            player.addInventoryItem(dingedSword);
            isLooted = true;
            return world.getGameMessageDefinitions().get(18).getMessage() + dingedSword.getName();
        }
        else return world.getGameMessageDefinitions().get(19).getMessage();
    }
}
