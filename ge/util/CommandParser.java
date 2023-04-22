package com.ge.util;

import com.ge.action.*;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CommandParser {
    private static ArrayList<ActionDef> actionDefs;
    public static ArrayList<ActionDef> getActionDefs(){
        if (actionDefs == null){
            loadActionDefs();
        }
        return actionDefs;
    }

    private static void loadActionDefs(){
        actionDefs = DataIO.loadActionDefs("def_command.xml");
    }

    //IMPORTANT NOTE THIS METHOD IS NOT POLYMORPHIC
    //IT TAKES A STRING AND RETURNS AN ACTION OBJECT
    public static Action parse(String input)
    {
        //takes in string of command(s) and translates to game events where applicable
        //we're going to start off with parsing single word and double words containing Verb or Verb Subject
        //e.g., go north, take item or the command could be just north

        String[] words = input.split(" ");
        String verb = words[0];


        //test if input is cheat
        if (input.startsWith("::")){
            Entity player = ApplicationWindow.getWorld().getPlayer();   //most, if not all cheats, involve the player somehow...
            Action actionCheat = new ActionCheat();

            if (verb.equalsIgnoreCase("::clearitems")){
                while (player.getInventory().size() > 0){
                    player.removeInventoryItem(player.getInventory().get(0));
                }
                String desc = "Clear all items.";
                actionCheat.setDescription(desc);
                player.changes.firePropertyChange(String.format("Activated Cheat - %s", desc), "old","new");
                return actionCheat;

            } else if (verb.equalsIgnoreCase("::item")){
                // The correct usage of this cheat is ::item itemid
                if (words.length == 2){
                    int itemId = Integer.parseInt(words[1]);
                    Item item = ApplicationWindow.getWorld().getItemDefs().get(itemId);
                    player.addInventoryItem(item);
                    String desc = String.format("Receive Item: %s", item.getName());
                    actionCheat.setDescription(desc);
                    player.changes.firePropertyChange(String.format("Activated Cheat - %s", desc), "old","new");
                    return actionCheat;
                } else {
                    ApplicationWindow.print("Cheat syntax ::item <itemid>, e.g. ::item 0");
                }
            } else if (verb.equalsIgnoreCase("::teleport")){
                // The correct usage of this cheat is ::teleport roomid
                if (words.length == 2){
                    int roomId = Integer.parseInt(words[1]);
                    Room room = ApplicationWindow.getWorld().getRoomById(roomId);
                    player.setRoom(room);
                    String desc = String.format("Teleport to: %s", room.getName());
                    actionCheat.setDescription(desc);
                    player.changes.firePropertyChange(String.format("Activated Cheat - %s", desc), "old","new");
                    return actionCheat;
                } else {
                    ApplicationWindow.print("Cheat syntax ::teleport <roomid>, e.g. ::teleport 5");
                }
            } else if (verb.equalsIgnoreCase("::cheats")){
                StringBuilder sb = new StringBuilder();
                sb.append("Available Cheats:\n");
                sb.append("::clearitems - @orange;Discards all inventory items.@\n");
                sb.append("::item <itemId> - @orange;Awards player with specified item.@\n");
                sb.append("::teleport <roomId> - @orange;Relocates player to specified room.@\n");
                ActionMessage actionMessage = new ActionMessage();
                actionMessage.setDescription(sb.toString());
                return actionMessage;
            }
            return new ActionUnrecognized();
        }

        StringJoiner sj = new StringJoiner(" ");
        for (int i=1; i < words.length; i++)
        {
            sj.add(words[i]);
        }
        String subject = sj.toString(); //space separated subject without the preceding action verb ,e.g. Wooden Shield

        for(ActionDef def: getActionDefs()){
            for (String v : def.getVerbs())
            {
                if (v.equalsIgnoreCase(verb)){
                    Action action;
                    if (def.getType().equalsIgnoreCase("Navigate")){
                        action = new ActionNavigate();
                    } else if (def.getType().equalsIgnoreCase("ItemRead")){
                        action = new ActionItemRead();
                    } else if (def.getType().equalsIgnoreCase("ItemTake")){
                        action = new ActionItemTake();
                    } else if (def.getType().equalsIgnoreCase("ItemDrop")){
                        action = new ActionItemDrop();
                    } else if (def.getType().equalsIgnoreCase("ItemConsume")){
                        action = new ActionItemConsume();
                    } else if (def.getType().equalsIgnoreCase("GameObjectInteract")){
                        action = new ActionGameObjectInteract();
                    } else if (def.getType().equalsIgnoreCase("Attack")){
                        action = new ActionAttack();
                    } else if (def.getType().equalsIgnoreCase("Dialog")){
                        action = new ActionDialog();
                    } else if (def.getType().equalsIgnoreCase("Inventory")){
                        action = new ActionInventory();
                    } else if (def.getType().equalsIgnoreCase("Info")){
                        action = new ActionInfo();
                    } else if (def.getType().equalsIgnoreCase("Examine")){
                        action = new ActionExamine();
                    } else if (def.getType().equalsIgnoreCase("Quit")){
                        action = new ActionQuit();
                    } else {
                        action = new ActionUnrecognized();
                    }

                    action.setVerb(verb);
                    action.setSubject(subject);
                    action.setDescription(def.getDescription());
                    action.setVerbs(def.getVerbs());
                    return action;
                }
            }

        }
        return new ActionUnrecognized();
    }
}
