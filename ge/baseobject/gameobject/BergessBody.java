package com.ge.baseobject.gameobject;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.general.World;
import com.ge.util.Constants;

public class BergessBody extends GameObject{


    private boolean isLooted = false;

    public BergessBody(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
    }

    @Override
    public String interact(World world) {
        if (!isLooted){
            isLooted = true;
            Entity player = world.getPlayer();
            Item keychain = world.getItemDefs().get(Constants.Items.AMULET_OF_GHOSTSPEAK);
            player.addInventoryItem(keychain);
            return world.getGameMessageDefinitions().get(14).getMessage();
        } else {
            return world.getGameMessageDefinitions().get(15).getMessage();
        }

    }
}
