package com.ge.baseobject.gameobject;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.general.World;
import com.ge.handler.HandlerRoomChange;
import com.ge.util.Constants;

public class Pond extends GameObject{
    private boolean isLooted = false;
    public Pond(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
    }

    @Override
    public String interact(World world) {
        if (!isLooted){
            if (HandlerRoomChange.getIsBangHeard()){
                Entity player = world.getPlayer();
                Item blade = world.getItemDefs().get(Constants.Items.SWORD_BLADE);
                player.addInventoryItem(blade);
                isLooted = true;
                return world.getGameMessageDefinitions().get(21).getMessage();
            } else {
                return world.getGameMessageDefinitions().get(22).getMessage();
            }
        } else {
            return world.getGameMessageDefinitions().get(23).getMessage();
        }

    }
}
