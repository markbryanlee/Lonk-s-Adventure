package com.ge.baseobject.gameobject;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.general.World;
import com.ge.util.Constants;

public class Tomb extends GameObject{

    private boolean isHilt = false;
    private boolean isBlade = false;
    private int state;


    public Tomb(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        super(id, name, examine, isDestructible, isInteractable);
        this.setState(0);
    }

    @Override
    public String interact(World world) {
        Item blade = world.getItemDefs().get(Constants.Items.SWORD_BLADE);
        Item hilt = world.getItemDefs().get(Constants.Items.SWORD_HILT);
        Entity player = world.getPlayer();

        Entity elder = world.getEntityDefs().get(Constants.Entities.ELDER);
        elder.setDialogState(4);

        Entity helga = world.getEntityDefs().get(Constants.Entities.HELGA);
        helga.setDialogState(5);

        Entity father = world.getEntityDefs().get(Constants.Entities.FATHER);
        father.setDialogState(5);

        if (player.hasInvetoryItemById(blade.getId()) && player.hasInvetoryItemById(hilt.getId())){
            return world.getGameMessageDefinitions().get(27).getMessage();
        } else if (player.hasInvetoryItemById(blade.getId())){
            return world.getGameMessageDefinitions().get(28).getMessage();
        } else if (player.hasInvetoryItemById(hilt.getId())){
            return world.getGameMessageDefinitions().get(29).getMessage();
        } else {
            return world.getGameMessageDefinitions().get(30).getMessage();
        }
    }

    public void setState(int state) {
        this.state = state;
    }
}
