package com.ge.baseobject.gameobject;

import com.ge.action.Action;
import com.ge.action.ActionInventory;
import com.ge.baseobject.item.Item;
import com.ge.baseobject.entity.Entity;
import com.ge.general.ApplicationWindow;
import com.ge.general.World;
import com.ge.handler.HandlerInteraction;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringJoiner;

public class ColorMixer extends GameObject{
    private Item colorA;
    private Item colorB;
    private final ArrayList<String> acceptedItemNames;
    private Entity player;
    private World world;

    ActionListener stdIn;
    ActionListener colorMixerIn;

    public ColorMixer(int id, String name, String examine, boolean isDestructible, boolean isInteractable){
        super(id, name, examine, isDestructible, isInteractable);
        acceptedItemNames = new ArrayList<>();
        acceptedItemNames.add("Red Vial");
        acceptedItemNames.add("Yellow Vial");
        acceptedItemNames.add("Blue Vial");
        acceptedItemNames.add("Green Vial");
        acceptedItemNames.add("Purple Vial");
        acceptedItemNames.add("Orange Vial");
    }

    @Override
    public String interact(World world) {
        stdIn = ApplicationWindow.input.getActionListeners()[0];
        ApplicationWindow.input.removeActionListener(stdIn);

        colorMixerIn = e -> interactionLoop();

        ApplicationWindow.input.addActionListener(colorMixerIn);
        player = world.getPlayer();
        this.world = world;
        ApplicationWindow.printItalic(String.valueOf(this));  //give feedback to user
        return "";
    }

    private void interactionLoop(){

        String rawInput = ApplicationWindow.input.getText();
        ApplicationWindow.printPlayer(">" + rawInput);  //display user types messages with > prefix to help distinguish player messages from game messages
        String action = commandParser(rawInput);

        if (action.length() > 0)
        {
            ApplicationWindow.printItalic(action);  //give feedback to user
        }

        ApplicationWindow.input.setText("");
        ApplicationWindow.scrollBottom();
    }

    //NOTE THIS IS A LOCAL COMMAND PARSER FOR THIS INTERACTION
    //IT IS UNIQUE TO THIS GAME OBJECT ONLY
    private String commandParser(String input){

        String[] words = input.split(" ");
        String verb = words[0];
        StringJoiner sj = new StringJoiner(" ");

        for (int i=1; i < words.length; i++)
        {
            sj.add(words[i]);
        }

        String subject = sj.toString();

        if (verb.equalsIgnoreCase("storage")) {
            return storage();
        } else if (verb.equalsIgnoreCase("take")) {
            return take(subject);
        } else if (verb.equalsIgnoreCase("insert")){
            return insert(subject) + "\n" + contains();
        } else if (verb.equalsIgnoreCase("remove")){
            return remove(subject) + "\n" + contains();
        } else if (verb.equalsIgnoreCase("mix")){
            return mix();
        } else if (verb.equalsIgnoreCase("exit")){
            return exit();
        } else if (verb.equalsIgnoreCase("inventory")){
            Action action = new ActionInventory();
            action.setVerb(verb);
            action.setSubject(subject);
            return HandlerInteraction.handleInventory(action);
        } else return commands();
    }

    private String storage(){
        StringBuilder sb = new StringBuilder();

        sb.append("There is an endless supply of:\n");

        for(String availableItem : acceptedItemNames){
            if (!availableItem.equalsIgnoreCase("Orange Vial")){
                sb.append(String.format("%s\n", availableItem));    //everything except orange vial can be taken from storage
            }
        }

        return sb.toString();
    }

    private String take(String subject){
        for(String itemName: acceptedItemNames){
            if (subject.equalsIgnoreCase(itemName) && !itemName.equalsIgnoreCase("Orange Vial")){
                player.addInventoryItem(world.getItemByName(itemName));
                player.changes.firePropertyChange(String.format("Take Item ->  %s", itemName), "old", "new");
                return String.format("You take %s", itemName);
            }
        }
        return String.format("Unable to take %s", subject);
    }

    private String insert(String subject) {
        if (colorA != null && colorB != null){
            return "The machine has no empty slots!\n";
        }
        boolean isMatched = false;
        for(String col : acceptedItemNames)
        {
            if (col.equalsIgnoreCase(subject)){
                isMatched = true;
                break;
            }
        }
        if (!isMatched){
            return "Unable to insert " + subject + " in to the machine.\n";
        }

        Item coloredVial = world.getItemByName(subject);
        if (colorA == null)
        {
            if (player.getInventory().contains(coloredVial)){
                player.removeInventoryItem(coloredVial);
                colorA = coloredVial;
                return "You placed " + coloredVial.getName() +" into Slot A\n";

            }
        } else
        {
            if (player.getInventory().contains(coloredVial)){
                player.removeInventoryItem(coloredVial);
                colorB = coloredVial;
                return "You placed " + coloredVial.getName() +" into Slot B\n";
            }
        }
        return "You don't seem to have " + subject + " in your inventory\n";
    }

    private String remove(String subject) {
        if (colorA != null && colorB != null){
            return "The machine has empty slots!";
        } else {
            if (colorA != null && colorA.getName().equalsIgnoreCase(subject))
            {
                Item vial = world.getItemByName(subject);
                player.addInventoryItem(vial);
                colorA = null;
                return "You take " + subject + " from Slot A";
            } else if (colorB != null && colorB.getName().equalsIgnoreCase(subject))
            {
                Item vial = world.getItemByName(subject);
                player.addInventoryItem(vial);
                colorB = null;
                return "You take " + subject + " from Slot B";
            }
        }
        return "Nothing interesting happens";
    }

    private String contains(){
        StringBuilder sb = new StringBuilder();

        sb.append("The Color Mixer machine contains:\n");

        if (colorA == null){
            sb.append("Slot A: empty\n");
        } else {
            sb.append(String.format("Slot A: %s\n", colorA.getName()));
        }

        if (colorB == null){
            sb.append("Slot B: empty\n");
        } else {
            sb.append(String.format("Slot B: %s\n", colorB.getName()));
        }
        return sb.toString();
    }

    private String commands(){
        return """
                To check storage, type @yellow;storage@
                To take a colored vial from storage, type @yellow;take <item>@
                To insert an item to the machine slots, type @yellow;insert <item>@
                To remove an item to the machine, type @yellow;remove <item>@
                To mix two items together, type @yellow;mix@
                To close the machine interaction, type @yellow;exit@""";
    }

    private String mix(){
        //mix color A and B
        if (colorA == null || colorB == null){
            return "Both Slots must contain a color in order to mix!";
        } else {
            ArrayList<String> slotColors = new ArrayList<>();
            slotColors.add(colorA.getName());
            slotColors.add(colorB.getName());

            if (slotColors.contains("Red Vial") && slotColors.contains("Yellow Vial")){
                Item newColor = world.getItemByName("Orange Vial");
                player.addInventoryItem(newColor);
                colorA = null;
                colorB = null;
                return "You receive " + newColor.getName() + " from the machine dispenser";
            } else if (slotColors.contains("Red Vial") && slotColors.contains("Blue Vial")){
                Item newColor = world.getItemByName("Purple Vial");
                player.addInventoryItem(newColor);
                colorA = null;
                colorB = null;
                return "You receive " + newColor.getName() + " from the machine dispenser";
            } else if (slotColors.contains("Yellow Vial") && slotColors.contains("Blue Vial")){
                Item newColor = world.getItemByName("Green Vial");
                player.addInventoryItem(newColor);
                colorA = null;
                colorB = null;
                return "You receive " + newColor.getName() + " from the machine dispenser";
            } else if (slotColors.contains("Blue Vial") && slotColors.contains("Orange Vial")){
                Item newColor = world.getItemByName("Brown Vial");
                player.addInventoryItem(newColor);
                colorA = null;
                colorB = null;
                return "You receive " + newColor.getName() + " from the machine dispenser";
            } else
            {
                String result = String.format("The machine is unable to mix %s and %s together\nThe items have been returned to your inventory.", colorA.getName(), colorB.getName());
                player.addInventoryItem(colorA);
                player.addInventoryItem(colorB);
                colorA = null;
                colorB = null;
                return result;
            }
        }
    }

    private String exit(){
        ApplicationWindow.input.removeActionListener(colorMixerIn);
        ApplicationWindow.input.addActionListener(stdIn);
        return player.getRoom().getExamine();
    }

    public String toString(){
        return contains() + commands();
    }
}
