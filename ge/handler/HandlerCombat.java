package com.ge.handler;

import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.gameobject.GameObject;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;
import com.ge.general.World;
import com.ge.util.Constants;

public class HandlerCombat {
    public static String handle(World world, Entity attacker, Entity defender){
        if (attacker.hasWeapon()) {
            if (defender.getId() == Constants.Entities.BERGESS){
                Room room = defender.getRoom();
                room.removeEntity(defender);

                GameObject body = world.getGameObjectDefs().get(Constants.GameObjects.BODY_BERGESS);
                room.addGameObject(body);
                room.setExamine("");
                return world.getGameMessageDefinitions().get(31).getMessage();
            } else if (defender.getId() == Constants.Entities.DRAGON){
                //evaluate if the dragon is distracted or not
                Entity loyalMage = world.getEntityDefs().get(Constants.Entities.MAGE);

                if (loyalMage.getDialogState() == 7) {
                    //the mage is distracting the dragon

                    ApplicationWindow.print(world.getGameMessageDefinitions().get(33).getMessage());
                    ApplicationWindow.print(world.getGameMessageDefinitions().get(34).getMessage());

                    Entity helga = world.getEntityDefs().get(Constants.Entities.HELGA);
                    helga.setDialogState(9);
                    loyalMage.setDialogState(8);

                    GameObject body = world.getGameObjectDefs().get(Constants.GameObjects.BODY_DRAGON);
                    Room room = loyalMage.getRoom();
                    room.addGameObject(body);

                    return world.getGameMessageDefinitions().get(35).getMessage();
                } else {
                    return world.getGameMessageDefinitions().get(36).getMessage();
                }
            }
        }
        return world.getGameMessageDefinitions().get(32).getMessage(); //display must have weapon text
    }
}
