package com.ge.handler;

import com.ge.action.Action;
import com.ge.action.ActionAttack;
import com.ge.action.ActionDef;
import com.ge.baseobject.item.Item;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.gameobject.GameObject;
import com.ge.general.*;
import com.ge.util.CommandParser;
import com.ge.util.Constants;

import java.util.ArrayList;
import java.util.StringJoiner;

public class HandlerInteraction {
    public static String handleInteractionGameObject(Action action){
        World world = ApplicationWindow.getWorld();
        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(72); //interact with what?

        GameObject object = world.getRoomGameObjectByName(subject);

        try {
            if (object.getIsInteractable() && object != null)
            {
                String result = object.interact(world);
                world.actionHistoryAdd(action);
                return result;
            }
        } catch (Exception ex){

        }


        return world.getGameMessageDefinitions().get(66).getMessage(); //default feedback if unimplemented
    }

    public static String handleDialog(Action action){
        World world = ApplicationWindow.getWorld();

        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(71).getMessage();   //talk with who?

        String npcName = action.getSubject();
        Entity npc = world.getRoomEntityByName(npcName);

        //the npc must be in the same room...
        if (npc != null)
        {
            world.actionHistoryAdd(action);
            Entity player = world.getPlayer();
            return HandlerDialog.handle(world, player, npc);
        }

        //default feedback if unimplemented
        //"Can't identify npcName in the room"
        return world.getGameMessageDefinitions().get(65).getMessage() + npcName + world.getGameMessageDefinitions().get(62).getMessage();
    }

    public static String handleItemDrop(Action action){
        World world = ApplicationWindow.getWorld();
        //attempt to take an item in current room
        Entity entity = world.getPlayer();
        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(69); //drop what?


        ArrayList<Item> entityItems = entity.getInventory();
        for (int i=0; i < entityItems.size(); i++){
            Item item = entity.getInventory().get(i);
            if (item.getName().equalsIgnoreCase(subject))
            {
                entity.getRoom().addItem(item);
                entity.removeInventoryItem(item);
                world.actionHistoryAdd(action);
                return world.getGameMessageDefinitions().get(64).getMessage() + verb + " " + item;
            }
        }

        return world.getGameMessageDefinitions().get(61).getMessage() + subject + world.getGameMessageDefinitions().get(63).getMessage(); //default feedback if unimplemented
    }

    public static String handleTakeItem(Action action){
        //attempt to take an item in current room
        World world = ApplicationWindow.getWorld();
        Entity entity = world.getPlayer();
        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(69);

        ArrayList<Item> roomItems = entity.getRoom().getItems();
        for (int i=0; i < roomItems.size(); i++) {
            Item item = entity.getRoom().getItems().get(i);
            if (item.getName().equalsIgnoreCase(subject)) {
                entity.getRoom().removeItem(item);
                entity.addInventoryItem(item);
                world.actionHistoryAdd(action);
                return world.getGameMessageDefinitions().get(64).getMessage() + verb + " " + item.getName();
            }
        }
        return world.getGameMessageDefinitions().get(61).getMessage() + subject + world.getGameMessageDefinitions().get(62).getMessage(); //default feedback if unimplemented
    }

    public static String handleReceiveItem(Action action){
        //the player receives an item
        World world = ApplicationWindow.getWorld();
        Entity player = world.getPlayer();
        String subject = action.getSubject();   //data is stored as string, though its containing item id
        int itemId = Integer.valueOf(subject);  //necessary cast
        Item item = world.getItemDefs().get(itemId);
        player.addInventoryItem(item);
        world.actionHistoryAdd(action);
        return world.getGameMessageDefinitions().get(60).getMessage() + item.getName();
    }

    public static String handleRoomChange(Action action){
        World world = ApplicationWindow.getWorld();
        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(73); // go where?

        return HandlerRoomChange.handle(world, action);
    }

    public static String handleExamine(Action action){
        //note since the command is 'examine <object>' - we don't know what type of object it is from the get go
        //however the player can only examine things around them, so we only look for objects contained in the current room
        World world = ApplicationWindow.getWorld();

        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(69); //examine what?


        Entity entity = world.getPlayer();
        if (subject.equalsIgnoreCase("room")){
            return entity.getRoom().getExamine() + "\n" + entity.getRoom().toString();
        } else {

            //check if an item in the player's inventory matches the subject
            for(Item item: entity.getInventory())
            {
                if (item.getName().equalsIgnoreCase(subject)){
                    world.actionHistoryAdd(action);
                    return item.getExamine();
                }
            }

            //check if the items in the room match the subject
            for(Item item: entity.getRoom().getItems())
            {
                if (item.getName().equalsIgnoreCase(subject)){
                    world.actionHistoryAdd(action);
                    return item.getExamine();
                }
            }

            //check if the Npcs in the room match the subject
            for(Entity ent: entity.getRoom().getEntities())
            {
                if (ent.getName().equalsIgnoreCase(subject)){
                    world.actionHistoryAdd(action);
                    return ent.getExamine();
                }
            }

            //check if the Npcs in the room match the subject
            for(GameObject go: entity.getRoom().getGameObjects())
            {
                if (go.getName().equalsIgnoreCase(subject)){
                    world.actionHistoryAdd(action);
                    return go.getExamine();
                }
            }

            //maybe the player is examining myself
            if (entity.getName().equalsIgnoreCase(subject)){
                world.actionHistoryAdd(action);
                return entity.getExamine();
            }
        }

        return world.getGameMessageDefinitions().get(59).getMessage() + subject; //default feedback if unimplemented
    }

    public static String handleAttack(ActionAttack action){
        World world = ApplicationWindow.getWorld();
        String verb = action.getVerb();
        String target = action.getSubject();

        if (target.length() == 0)
            return verb + world.getGameMessageDefinitions().get(70).getMessage();   //attack who?

        Entity attacker = world.getPlayer();
        Entity defender = world.getRoomEntityByName(action.getSubject());

        if (defender != null && defender.getIsAttackable())
        {
            world.actionHistoryAdd(action);
            return HandlerCombat.handle(world, attacker, defender);
        } else if (defender.getId() == Constants.Entities.HELGA)
        {
            return world.getGameMessageDefinitions().get(51).getMessage();
        } else if (defender.getId() == Constants.Entities.FATHER)
        {
            return world.getGameMessageDefinitions().get(52).getMessage();
        } else if (defender.getId() == Constants.Entities.BERGESS)
        {
            return world.getGameMessageDefinitions().get(53).getMessage();
        } else {
            return world.getGameMessageDefinitions().get(55).getMessage()+ target; //default feedback if unimplemented
        }

    }

    public static String handleInfo(Action action){
        StringBuilder sb = new StringBuilder();
        World world = ApplicationWindow.getWorld();
        world.actionHistoryAdd(action);

        sb.append(world.getGameMessageDefinitions().get(58).getMessage() + ":\n");
        for(ActionDef a : CommandParser.getActionDefs()){
            sb.append(String.format("%s verbs: @yellow;%s@\n", a.getDescription(), String.join(", ", a.getVerbs())));
        }

        return sb.toString();
    }

    public static String handleConsumeItem(Action action){
        World world = ApplicationWindow.getWorld();
        String verb = action.getVerb();
        String subject = action.getSubject();

        if (subject.length() == 0)
            return verb + world.getGameMessageDefinitions().get(69); //consume what?

        Item item = world.getRoomItemByName(subject);

        if (item != null)
        {
            world.actionHistoryAdd(action);
            return item.interact(world);
        }

        return world.getGameMessageDefinitions().get(66).getMessage(); //default feedback if unimplemented
    }

    public static String handleInventory(Action action){
        World world = ApplicationWindow.getWorld();
        world.actionHistoryAdd(action);

        Entity entity = ApplicationWindow.getWorld().getPlayer();
        StringJoiner joiner = new StringJoiner("\n");

        for(Item item: entity.getInventory()){
            joiner.add(item.getName());
        }

        if (joiner.length() == 0)
        {
            return world.getGameMessageDefinitions().get(54).getMessage();
        }

        return world.getGameMessageDefinitions().get(56).getMessage() + ":\n" + joiner;
    }

    public static String handleQuit(){
        World world = ApplicationWindow.getWorld();
        world.setIsGameOver(true);
        return world.getGameMessageDefinitions().get(57).getMessage();
    }
}
