package com.ge.handler;

import com.ge.action.Action;
import com.ge.action.ActionItemReceive;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.item.Item;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;
import com.ge.general.Prompt;
import com.ge.general.Response;
import com.ge.general.World;
import com.ge.util.CommandParser;
import com.ge.util.Constants;
import java.awt.event.ActionListener;

public class HandlerDialog {

    private static ActionListener stdIn;
    private static ActionListener dialogIn;

    private static Entity instanceNpc;
    private static Entity instancePlayer;
    private static World instanceWorld;
    private static Prompt activePrompt;

    public static String handle(World world, Entity player, Entity npc){
        instanceNpc = npc;
        instancePlayer = player;
        instanceWorld = world;

        stdIn = ApplicationWindow.input.getActionListeners()[0];
        ApplicationWindow.input.removeActionListener(stdIn);
        dialogIn = e -> interactionLoop();
        ApplicationWindow.input.addActionListener(dialogIn);

        return getDialogMessage();
    }

    private static void interactionLoop(){

        String rawInput = ApplicationWindow.input.getText();
        ApplicationWindow.printPlayer(">" + rawInput);  //display user types messages with > prefix to help distinguish player messages from game messages
        String action = parseInput(rawInput);

        if (action.length() > 0)
        {
            ApplicationWindow.printItalic(action);  //give feedback to user
        }

        ApplicationWindow.updateUI();
        ApplicationWindow.input.setText("");
        ApplicationWindow.scrollBottom();

    }

    private static String parseInput(String input){
        //identify which dialog response was chosen

        boolean isValid = isInteger(input); //test if integer

        if (isValid){
            int idx = Integer.valueOf(input);

            if (idx-1 <= activePrompt.getResponses().length){
                Response r = activePrompt.getResponses()[idx-1];
                int next = r.getFollowingState();


                if (next == Constants.Dialog.CLOSE_DIALOG_NO_CHANGE){
                    return exit();
                }
                instanceNpc.setDialogState(next); //set next dialog state
                if (next != Constants.Dialog.CLOSE_DIALOG_WITH_RESET){
                    return getDialogMessage();
                } else {
                    return exit();
                }
            }


        } else {
            exit();
            Action action = CommandParser.parse(input);

            ApplicationWindow.executeAction(action);

        }

        return instanceWorld.getGameMessageDefinitions().get(13).getMessage();
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }


    private static String exit(){
        ApplicationWindow.input.removeActionListener(dialogIn);
        ApplicationWindow.input.addActionListener(stdIn);
        return instanceWorld.getGameMessageDefinitions().get(68).getMessage();
    }

    private static void resetDialogState(){
        instanceNpc.setDialogState(0);
    }

    private static String getDialogMessage(){
        //reset npc dialogState (used in certain situations)
        if (instanceNpc.getDialogState() == Constants.Dialog.CLOSE_DIALOG_WITH_RESET){
            resetDialogState();
        }

        //check if special states match (dialog state & specific item requirements)
        if (instanceNpc.getId() == Constants.Entities.FATHER){
            return dialogFather();
        } else if (instanceNpc.getId() == Constants.Entities.HELGA){
            return dialogHelga();
        } else if (instanceNpc.getId() == Constants.Entities.MAGE){
            return dialogLoyalMage();
        } else if (instanceNpc.getId() == Constants.Entities.BERGESS){
            return dialogBergess();
        } else if (instanceNpc.getId() == Constants.Entities.SPOOKY_GHOST){
            return dialogSpookyGhost();
        } else if (instanceNpc.getId() == Constants.Entities.BARTENDER){
            return dialogBartender();
        } else if (instanceNpc.getId() == Constants.Entities.PRIEST){
            return dialogPriest();
        }

        //if we make it this far then follow generic dialog state match
        return matchPrompt();

    }

    private static String matchPrompt() {
        boolean isPromptFound = false;
        for (Prompt p : instanceWorld.getPromptDefinitions()){
            if (p.getEntity() == instanceNpc){
                if (p.getDialogState() == instanceNpc.getDialogState()){
                    //show prompt based on dialog state

                    ApplicationWindow.print(p.getMessage());

                    //mechanism for no response options
                    if (p.getDefaultNextState() > -1){
                        instanceNpc.setDialogState(p.getDefaultNextState());
                        return exit();
                    } else {
                        //display response options
                        int counter = 1;
                        for(Response r : p.getResponses()){
                            ApplicationWindow.printItalic(String.format("(%d) ", counter) + r.getResponseMessage());
                            counter += 1;
                        }

                        activePrompt = p;
                        isPromptFound = true;
                    }

                    break;
                }
            }
        }

        if (!isPromptFound)
            return instanceNpc.getName() + instanceWorld.getGameMessageDefinitions().get(0).getMessage();
        else return "";
    }

    private static String dialogFather(){
        //handle special case in late game
        if (instanceNpc.getDialogState() == 5 && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_BLADE) && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_HILT)){
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(1).getMessage());
            ApplicationWindow.printItalic(instanceWorld.getDialogResponseDefs().get(108).getMessage());
            return exit();
        }

        return matchPrompt();
    }

    private static String dialogHelga(){
        //handle special case  - give player letter if they dont have it
        if (instanceNpc.getDialogState() == 2 && !instancePlayer.hasInvetoryItemById(Constants.Items.HELGAS_LETTER)){
            ActionItemReceive action = new ActionItemReceive();
            action.setSubject(String.valueOf(Constants.Items.HELGAS_LETTER));
            action.setDescription("[Action] Received Item");    //this is purely debug info here (not part of actual game data)
            action.execute();

            //also update father dialog state
            Entity father = instanceWorld.getEntityDefs().get(Constants.Entities.FATHER);
            father.setDialogState(5);
        }

        //handle special case  - give player letter if they dont have it
        else if (instanceNpc.getDialogState() == 5 && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_BLADE) && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_HILT)){
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(2).getMessage());
            ApplicationWindow.printItalic(instanceWorld.getDialogResponseDefs().get(109).getMessage());
            return exit();
        } else if (instanceNpc.getDialogState() == 7){

            if (instancePlayer.hasInvetoryItemById(Constants.Items.LEGENDARY_SWORD)){
                ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(3).getMessage());

                //FINAL FIGHT
                //update game states here, prepare for final battle
                Entity lonk = instancePlayer;
                Entity helga = instanceNpc;
                Entity loyalMage = instanceWorld.getEntityDefs().get(Constants.Entities.MAGE);

                Room oldRoomLonk = lonk.getRoom();
                oldRoomLonk.removeEntity(lonk);

                Room oldRoomHelga = helga.getRoom();
                oldRoomHelga.removeEntity(helga);

                Room oldRoomMage = loyalMage.getRoom();
                oldRoomMage.removeEntity(loyalMage);

                //"Teleport effect"
                Room emperorTower = instanceWorld.getRoomById(Constants.Rooms.EMPEROR_DRAGONS_TOWER);
                emperorTower.addEntity(lonk);
                emperorTower.addEntity(helga);
                emperorTower.addEntity(loyalMage);

                helga.setDialogState(8);
                loyalMage.setDialogState(6);

                return exit();

            } else {
                ApplicationWindow.printItalic(instanceWorld.getDialogResponseDefs().get(110).getMessage());
                instanceNpc.setDialogState(6);
                return exit();
            }
        } else if (instanceNpc.getDialogState() == 9){
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(4).getMessage());
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(5).getMessage());
            instanceWorld.setIsGameOver(true);
            return exit();



        }

        return matchPrompt();
    }

    private static String dialogLoyalMage(){
        if (instanceNpc.getDialogState() == 0 && instancePlayer.hasInvetoryItemById(Constants.Items.BROWN_VIAL)){

            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(6).getMessage());
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(7).getMessage());
            ApplicationWindow.printItalic(instanceWorld.getDialogResponseDefs().get(111).getMessage());

            //remove Brown Vial
            Item brownVial = instanceWorld.getItemDefs().get(Constants.Items.BROWN_VIAL);
            instancePlayer.removeInventoryItem(brownVial);

            instanceNpc.setDialogState(2);
        } else if (instanceNpc.getDialogState() == 2 && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_BLADE) && instancePlayer.hasInvetoryItemById(Constants.Items.SWORD_HILT)){

            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(8).getMessage());
            ApplicationWindow.printItalic(instanceWorld.getDialogResponseDefs().get(112).getMessage());

            Item blade = instanceWorld.getItemDefs().get(Constants.Items.SWORD_BLADE);
            Item hilt = instanceWorld.getItemDefs().get(Constants.Items.SWORD_HILT);
            instancePlayer.removeInventoryItem(blade);
            instancePlayer.removeInventoryItem(hilt);

            ActionItemReceive action = new ActionItemReceive();
            action.setSubject(String.valueOf(Constants.Items.LEGENDARY_SWORD));
            action.setDescription("[Action] Received Item");    //this is purely debug info here (not part of actual game data)
            action.execute();

            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(9).getMessage());

            //update game states here, prepare for final battle
            Entity helga = instanceWorld.getEntityDefs().get(Constants.Entities.HELGA);
            Entity loyalMage = instanceNpc;
            Entity father = instanceWorld.getEntityDefs().get(Constants.Entities.FATHER);

            helga.setDialogState(6);
            loyalMage.setDialogState(5);
            father.setDialogState(6);

            Entity spookyGhost = instanceWorld.getEntityDefs().get(Constants.Entities.SPOOKY_GHOST);
            spookyGhost.setDialogState(5);
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(10).getMessage());


            return exit();
        }


        return matchPrompt();
    }

    private static String dialogSpookyGhost(){
        //handle special case  - player has amulet
        if (instanceNpc.getDialogState() == 0 && instancePlayer.hasInvetoryItemById(Constants.Items.AMULET_OF_GHOSTSPEAK)){
            instanceNpc.setDialogState(1);
        }

        return matchPrompt();
    }

    private static String dialogPriest(){
        //handle special case  - give player book
        if (instanceNpc.getDialogState() == 3){

            Item holyBook = instanceWorld.getItemDefs().get(Constants.Items.BOOK);
            if (!instancePlayer.hasInvetoryItemById(holyBook.getId()));
            {
                ActionItemReceive action = new ActionItemReceive();
                action.setSubject(String.valueOf(Constants.Items.BOOK));
                action.setDescription("[Action] Received Item");    //this is purely debug info here (not part of actual game data)
                action.execute();
            }
        }

        return matchPrompt();
    }

    private static String dialogBartender(){
        //handle special case  - give player beer
        if (instanceNpc.getDialogState() == 3){
            ActionItemReceive action = new ActionItemReceive();
            action.setSubject(String.valueOf(Constants.Items.BEER));
            action.setDescription("[Action] Received Item");    //this is purely debug info here (not part of actual game data)
            action.execute();
        }
        //handle special case  - activate priest
        else if (instanceNpc.getDialogState() == 4){

            Entity entity = instanceWorld.getEntityDefs().get(Constants.Entities.PRIEST);
            entity.setDialogState(1);
        }

        return matchPrompt();
    }

    private static String dialogBergess(){
        //handle special case  - bergess turned his back
        if (instanceNpc.getDialogState() == 3){
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(11).getMessage());
            instanceNpc.setDialogState(4);
            instanceNpc.setIsAttackable(true);
            return exit();
        }
        //handle special case  - bergess no longer has his back turned
        else if (instanceNpc.getDialogState() == 4){
            ApplicationWindow.print(instanceWorld.getGameMessageDefinitions().get(12).getMessage());
            instanceNpc.setDialogState(5);
            instanceNpc.setIsAttackable(false);
        }
        return matchPrompt();
    }
}
