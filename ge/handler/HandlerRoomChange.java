package com.ge.handler;

import com.ge.action.Action;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.gameobject.GameObject;

import com.ge.util.Constants;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;
import com.ge.general.World;

public class HandlerRoomChange {

    public static String handle(World world, Action action){
        //this is not going to be pretty...but there's 5 days left until presentation day as I will start implementing this...
        //ensure that the current room has a neighbour in provided direction
        String direction = action.getSubject();
        Entity entity = world.getPlayer();

        if (entity.getRoom().getId() == Constants.Rooms.EMPEROR_DRAGONS_TOWER){
            return world.getGameMessageDefinitions().get(37).getMessage();
        }

        int targetRoomId = Room.NEIGHBOUR_NONE;
        if (direction.equalsIgnoreCase("north"))
        {
            targetRoomId = entity.getRoom().getNeighbourNorth();
        } else if (direction.equalsIgnoreCase("south"))
        {
            targetRoomId = entity.getRoom().getNeighbourSouth();
        } else if (direction.equalsIgnoreCase("west"))
        {
            targetRoomId = entity.getRoom().getNeighbourWest();
        } else if (direction.equalsIgnoreCase("east"))
        {
            targetRoomId = entity.getRoom().getNeighbourEast();
        }

        if (targetRoomId != Room.NEIGHBOUR_NONE){
            Room newRoom = world.getRoomById(targetRoomId);

            //handle rooms with special conditional checks
            if (newRoom.getId() == Constants.Rooms.GREAT_SWAMP){
                return enterGreatSwamp(world, newRoom, action);
            } else if (newRoom.getId() == Constants.Rooms.EMPEROR_DRAGONS_TOWER){
                return world.getGameMessageDefinitions().get(38).getMessage();
            } else if (newRoom.getId() == Constants.Rooms.HAUNTED_ORCHARD){
                return enterHauntedOrchard(world, newRoom, action);
            } else if (newRoom.getId() == Constants.Rooms.MYSTERY_OF_THE_DESERT){
                return enterMysteryOfTheDesert(world, newRoom, action);
            } else if (newRoom.getId() >= Constants.Rooms.LOST_WOODS1 && newRoom.getId() <= Constants.Rooms.LOST_WOODS9){
                return enterLostWoods(world, newRoom, action);
            } else {
                if (!newRoom.getIsLocked()){
                    Room oldRoom = entity.getRoom();
                    oldRoom.removeEntity(entity);
                    newRoom.addEntity(entity);
                    entity.setMoves(entity.getMoves() + 1);
                    world.actionHistoryAdd(action);
                    ApplicationWindow.printBold("\n" + newRoom.getName());   //keep it here for now...
                    return newRoom.getExamine();
                } else {
                    return world.getGameMessageDefinitions().get(39).getMessage();
                }
            }
        }

        return world.getGameMessageDefinitions().get(40).getMessage() + direction;
    }

    private static String enterHauntedOrchard(World world, Room room, Action action){
        //trigger the Spooky Ghost message
        Entity spookyGhost = world.getEntityDefs().get(Constants.Entities.SPOOKY_GHOST);
        Entity player = world.getPlayer();
        if (spookyGhost.getDialogState() == 0){

            if (player.hasInvetoryItemById(Constants.Items.AMULET_OF_GHOSTSPEAK)){
                //enter room...
                ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...
                Room oldRoom = player.getRoom();
                oldRoom.removeEntity(player);
                player.setRoom(room);
                player.setMoves(player.getMoves() + 1);
                world.actionHistoryAdd(action);

            } else {
                ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...

                //the player cannot understand the ghost
                ApplicationWindow.print(world.getGameMessageDefinitions().get(41).getMessage());
                ApplicationWindow.print(world.getGameMessageDefinitions().get(42).getMessage());
                ApplicationWindow.print(world.getGameMessageDefinitions().get(43).getMessage());
                ApplicationWindow.print(world.getGameMessageDefinitions().get(44).getMessage());

                Room oldRoom = player.getRoom();
                oldRoom.removeEntity(player);
                player.setRoom(room);
                player.setMoves(player.getMoves() + 1);
                world.actionHistoryAdd(action);
            }
        } else if (spookyGhost.getDialogState() == 5){
            ApplicationWindow.print(world.getGameMessageDefinitions().get(45).getMessage());
        } else {
            //enter room...
            ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);
            player.setRoom(room);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
        }

        return room.getExamine();
    }

    private static String enterMysteryOfTheDesert(World world, Room room, Action action){

        ApplicationWindow.print("\n" + room.getExamine());   //keep it here for now...
        Entity loyalMage = world.getEntityDefs().get(Constants.Entities.MAGE);
        Entity player = world.getPlayer();
        if (loyalMage.getDialogState() <= 1){
            return world.getGameMessageDefinitions().get(46).getMessage();
        } else {
            //enter room...
            ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);
            player.setRoom(room);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
        }

        return room.getExamine();
    }

    private static String enterGreatSwamp(World world, Room room, Action action){
        //At first the player can only enter while possessing the letter from Helga
        //after solving the puzzle the player can come-go without the letter
        Entity loyalMage = world.getEntityDefs().get(Constants.Entities.MAGE);

        if (loyalMage.getDialogState() == 0){
            Entity player = world.getPlayer();

            //check if player has the letter from helga
            if (player.hasInvetoryItemById(Constants.Items.HELGAS_LETTER)){
                //player is allowed to enter
                Room oldRoom = player.getRoom();
                oldRoom.removeEntity(player);
                player.setRoom(room);
                player.setMoves(player.getMoves() + 1);
                world.actionHistoryAdd(action);
                ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...
                return room.getExamine();
            } else {
                ApplicationWindow.printItalic(world.getGameMessageDefinitions().get(47).getMessage());
            }

        } else if (loyalMage.getDialogState() > 0){
            //player is allowed to enter
            Entity player = world.getPlayer();
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);

            if (oldRoom.getId() == Constants.Rooms.MYSTERY_OF_THE_DESERT){
                //make sure to reset Bergess state
                Entity bergess = world.getEntityDefs().get(Constants.Entities.BERGESS);
                bergess.setDialogState(0);
            }



            player.setRoom(room);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
            ApplicationWindow.printBold("\n" + room.getName());   //keep it here for now...
            return room.getExamine();
        }

        return "";
    }


    private static boolean isBangHeard = false;
    public static boolean getIsBangHeard(){
        return isBangHeard;
    }
    private static String enterLostWoods(World world, Room newRoom, Action action){

        Entity loyalMage = world.getEntityDefs().get(Constants.Entities.MAGE);

        if (loyalMage.getDialogState() <= 1){
            return world.getGameMessageDefinitions().get(48).getMessage();
        }


        if (newRoom.getId() == Constants.Rooms.LOST_WOODS4) {
            Entity player = world.getPlayer();
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);
            newRoom.addEntity(player);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
            ApplicationWindow.printBold("\n" + newRoom.getName());   //keep it here for now...

            if (!isBangHeard){
                ApplicationWindow.print(world.getGameMessageDefinitions().get(49).getMessage());
                isBangHeard = true;

                //place an object hiding the sword hilt somewhere
                Room lw1 = world.getRoomById(Constants.Rooms.LOST_WOODS1);
                GameObject rock = world.getGameObjectDefs().get(Constants.GameObjects.ROCK);
                lw1.removeGameObject(rock);

                GameObject strangeRock = world.getGameObjectDefs().get(Constants.GameObjects.STRANGE_ROCK);
                lw1.addGameObject(strangeRock);

                return newRoom.getExamine() + "\n" + world.getGameMessageDefinitions().get(50).getMessage();
            }

        } else if (newRoom.getId() == Constants.Rooms.LOST_WOODS6){
            Entity player = world.getPlayer();
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);
            newRoom.addEntity(player);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
            ApplicationWindow.printBold("\n" + newRoom.getName());   //keep it here for now...

            if (!isBangHeard){

                isBangHeard = true;

                //place an object hiding the sword hilt somewhere
                Room lw1 = world.getRoomById(Constants.Rooms.LOST_WOODS1);
                GameObject rock = world.getGameObjectDefs().get(Constants.GameObjects.ROCK);
                lw1.removeGameObject(rock);

                GameObject strangeRock = world.getGameObjectDefs().get(Constants.GameObjects.STRANGE_ROCK);
                lw1.addGameObject(strangeRock);

                return newRoom.getExamine() + "\n" + world.getGameMessageDefinitions().get(50).getMessage();
            }

        } else {
            Entity player = world.getPlayer();
            Room oldRoom = player.getRoom();
            oldRoom.removeEntity(player);
            newRoom.addEntity(player);
            player.setMoves(player.getMoves() + 1);
            world.actionHistoryAdd(action);
            ApplicationWindow.printBold("\n" + newRoom.getName());   //keep it here for now...
        }


        return newRoom.getExamine();
    }

}
