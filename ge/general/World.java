package com.ge.general;

import com.ge.action.Action;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.entity.Player;
import com.ge.baseobject.gameobject.*;
import com.ge.baseobject.item.Item;
import com.ge.util.Constants;
import com.ge.baseobject.room.Room;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class World implements java.io.Serializable{
    //World is a container of rooms
    private Entity player;

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
    public PropertyChangeSupport getChanges(){
        return this.changes;
    }

    private boolean isGameOver;
    public boolean getIsGameOver(){
        return isGameOver;
    }
    public void setIsGameOver(boolean flag){
        isGameOver = flag;
    }

    private String greetingMessage;
    public String getGreetingMessage(){
        return greetingMessage;
    }
    public void setGreetingMessage(String message)
    {
        greetingMessage = message;
    }

    private ArrayList<Entity> entityDefinitions;
    public ArrayList<Entity> getEntityDefs(){
        return entityDefinitions;
    }
    public void setEntityDefs(ArrayList<Entity> entityDefs){
        entityDefinitions = entityDefs;
    }
    private ArrayList<Item> itemDefinitions;
    public ArrayList<Item> getItemDefs(){
        return itemDefinitions;
    }
    public void setItemDefs(ArrayList<Item> itemDefs){
        itemDefinitions = itemDefs;
    }
    private ArrayList<Room> roomDefinitions;
    public ArrayList<Room> getRoomDefs(){
        return roomDefinitions;
    }
    public void setRoomDefs(ArrayList<Room> roomDefs){
        roomDefinitions = roomDefs;
    }

    private ArrayList<GameObject> gameObjectDefinitions;
    public ArrayList<GameObject> getGameObjectDefs(){
        return this.gameObjectDefinitions;
    }
    public void setGameObjectDefs(ArrayList<GameObject> gameObjectDefs){
        this.gameObjectDefinitions = gameObjectDefs;
    }

    private ArrayList<DialogResponse> dialogResponseDefinitions;
    public ArrayList<DialogResponse> getDialogResponseDefs(){
        return this.dialogResponseDefinitions;
    }
    public void setDialogResponseDefs(ArrayList<DialogResponse> dialogResponseDefs){
        this.dialogResponseDefinitions = dialogResponseDefs;
    }

    private ArrayList<DialogResponse> gameMessageDefinitions;
    public ArrayList<DialogResponse> getGameMessageDefinitions(){
        return gameMessageDefinitions;
    }
    public void setGameMessageDefinitions(ArrayList<DialogResponse> gameMessageDefs){
        this.gameMessageDefinitions = gameMessageDefs;
    }

    private ArrayList<Prompt> promptDefinitions;


    private ArrayList<Action> actionHistory = new ArrayList<>();
    public void actionHistoryAdd(Action action){
        changes.firePropertyChange(String.format("%s -> %s", action.getDescription(), action.getSubject()), null, action.getDescription());
        actionHistory.add(action);
    }

    public Entity getPlayer(){
        if (this.player == null){
            player = new Player(1, "Lonk", "Lonk stands a tall 6’3, much taller than he was when he was younger.", false);
            Room room = getRoomById(Constants.Rooms.LONKS_HOUSE);
            room.addEntity(player);
        }
        return this.player;
    }

    public void setPlayer(Entity ent){
        this.player = ent;
    }

    public Room getRoomById(int id){
        for(Room room: roomDefinitions)
        {
            if (room.getId() == id)
            {
                return room;
            }
        }
        return null;
    }

    public Entity getRoomEntityByName(String objectName) {
        for(Entity e : getPlayer().getRoom().getEntities())
        {
            if (e.getName().equalsIgnoreCase(objectName)){
                return e;
            }
        }
        return null;
    }

    public GameObject getRoomGameObjectByName(String objectName) {
        for(GameObject go : getPlayer().getRoom().getGameObjects())
        {
            if (go.getName().equalsIgnoreCase(objectName)){
                return go;
            }
        }
        return null;
    }

    public Item getRoomItemByName(String objectName) {
        for(Item i : getPlayer().getInventory())
        {
            if (i.getName().equalsIgnoreCase(objectName)){
                return i;
            }
        }
        for(Item i : getPlayer().getRoom().getItems())
        {
            if (i.getName().equalsIgnoreCase(objectName)){
                return i;
            }
        }
        return null;
    }

    public Item getItemByName(String name) {
        for (Item item : getItemDefs()){
            if (item.getName().equalsIgnoreCase(name)){
                return item;
            }
        }
        return null;
    }

    public GameObject getGameObjectByName(String name) {
        for (GameObject object : getGameObjectDefs()){
            if (object.getName().equalsIgnoreCase(name)){
                return object;
            }
        }
        return null;
    }

    public World(){

    }

    //Game objects have unique functionality
    //load them programmatically...
    public void loadGameObjects(){
        ArrayList<GameObject> gameObjectDefinitions = new ArrayList<>();

        //Create Chest
        GameObject chest = new Chest(0, "Chest", "I wonder whats inside?", false, true);
        chest.setRoom(getRoomById(Constants.Rooms.LONKS_HOUSE));
        gameObjectDefinitions.add(chest);


        GameObject colorMixer = new ColorMixer(1, "Color Mixer", "A device that mixes colors.", false, true);
        colorMixer.setRoom(getRoomById(Constants.Rooms.GREAT_SWAMP));
        gameObjectDefinitions.add(colorMixer);
//
        GameObject tomb = new Tomb(2, "Tomb", "TODO", false, true);
        tomb.setRoom(getRoomById(Constants.Rooms.CAVE));
        gameObjectDefinitions.add(tomb);


        GameObject pond = new Pond(3, "Pond", "A medium sized pond in the middle of the woods.", false, true);
        pond.setRoom(getRoomById(Constants.Rooms.LOST_WOODS5));
        gameObjectDefinitions.add(pond);


        GameObject bushes = new Bushes(4, "Bushes", "A bunch of leaves and branches.", false, true);
        bushes.setRoom(getRoomById(Constants.Rooms.LOST_WOODS3));
        gameObjectDefinitions.add(bushes);

        GameObject rock = new Rock(5, "Rock", "I bet it's heavy.", false, true);
        rock.setRoom(getRoomById(Constants.Rooms.LOST_WOODS1));
        gameObjectDefinitions.add(rock);

        GameObject strangeRock = new StrangeRock(6, "Rock", "I swear this rock looked different before...", false, true);
        gameObjectDefinitions.add(strangeRock);

        GameObject bergessBody = new BergessBody(7, "Body", "The dead body of Bergess.", false, true);
        gameObjectDefinitions.add(bergessBody);

        GameObject dragonsBody = new DragonsBody(8, "Dragon's Corpse", "The lost cause of havoc.", false, false);
        gameObjectDefinitions.add(dragonsBody);

        setGameObjectDefs(gameObjectDefinitions);
    }

    public void loadPrompts(){
        ArrayList<Prompt> prompts = new ArrayList<>();

        //FATHER
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                0,
                dialogResponseDefinitions.get(0).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(1).getMessage(), 1),
                        new Response(dialogResponseDefinitions.get(2).getMessage(), 2)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                1,
                dialogResponseDefinitions.get(3).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(4).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(5).getMessage(), 4)}
        ));
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                2,
                dialogResponseDefinitions.get(6).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(7).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(8).getMessage(), 0)}
        ));
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                3,
                dialogResponseDefinitions.get(9).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(10).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(11).getMessage(), 1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                4,
                dialogResponseDefinitions.get(12).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(13).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(14).getMessage(), 1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                5,
                dialogResponseDefinitions.get(15).getMessage(),
                new Response[]{},
                5

        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.FATHER),
                6, //final battle state
                dialogResponseDefinitions.get(16).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(17).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE),
                        new Response(dialogResponseDefinitions.get(18).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));



        //QUEEN HELGA
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                0,
                dialogResponseDefinitions.get(19).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(20).getMessage(), 2),
                        new Response(dialogResponseDefinitions.get(21).getMessage(), 1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                1,
                dialogResponseDefinitions.get(22).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(23).getMessage(), 2),
                        new Response(dialogResponseDefinitions.get(24).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                2,
                dialogResponseDefinitions.get(25).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(26).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(27).getMessage(), 4)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                3,
                dialogResponseDefinitions.get(28).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(29).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(30).getMessage(), 2)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                4,
                dialogResponseDefinitions.get(31).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(32).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(33).getMessage(), 2)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                5,
                dialogResponseDefinitions.get(34).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(35).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE),
                        new Response(dialogResponseDefinitions.get(36).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                6, //final batle state
                dialogResponseDefinitions.get(37).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(38).getMessage(), 7),  //teleport npcs to final fight
                        new Response(dialogResponseDefinitions.get(39).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                8, //IN THE FIGHT
                dialogResponseDefinitions.get(40).getMessage(),
                new Response[]{},
                8
        ));


        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.HELGA),
                9, //VICTORY SPEECH!
                dialogResponseDefinitions.get(41).getMessage(),
                new Response[]{},
                9 //game over state
        ));

        //LOYAL MAGE
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                0,
                dialogResponseDefinitions.get(42).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(43).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(44).getMessage(), 1),
                        new Response(dialogResponseDefinitions.get(45).getMessage(), 4)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                1,
                dialogResponseDefinitions.get(46).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(47).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(48).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                2,
                dialogResponseDefinitions.get(49).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(50).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(51).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                3,
                dialogResponseDefinitions.get(52).getMessage(),
                new Response[]{},
                2
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                4,
                dialogResponseDefinitions.get(53).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(54).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(55).getMessage(), 0)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                5, //final batle state
                dialogResponseDefinitions.get(56).getMessage(),
                new Response[]{}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                6, //IN THE FIGHT
                dialogResponseDefinitions.get(57).getMessage(),
                new Response[]{},
                7
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.MAGE),
                8, //VICTORY SPEECH!
                dialogResponseDefinitions.get(58).getMessage(),
                new Response[]{},
                8
        ));

        //SPOOKY GHOST
        //default response when no amulet is worn
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.SPOOKY_GHOST),
                0,
                dialogResponseDefinitions.get(59).getMessage(),
                new Response[]{},
                1
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.SPOOKY_GHOST),
                1,
                dialogResponseDefinitions.get(60).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(61).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(62).getMessage(), 2),
                        new Response(dialogResponseDefinitions.get(63).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.SPOOKY_GHOST),
                2,
                dialogResponseDefinitions.get(64).getMessage(),
                new Response[]{},
                1
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.SPOOKY_GHOST),
                3,
                dialogResponseDefinitions.get(65).getMessage(),
                new Response[]{},
                1
        ));

        //Village Elder
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                0,
                dialogResponseDefinitions.get(66).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(67).getMessage(), 1),
                        new Response(dialogResponseDefinitions.get(68).getMessage(), 2)}
        ));
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                1,
                dialogResponseDefinitions.get(69).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(70).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(71).getMessage(), -1),
                        new Response(dialogResponseDefinitions.get(72).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                2,
                dialogResponseDefinitions.get(73).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(74).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(75).getMessage(), 1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                3,
                dialogResponseDefinitions.get(76).getMessage(),
                new Response[]{},
                1
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                4,
                dialogResponseDefinitions.get(77).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(78).getMessage(), 5),
                        new Response(dialogResponseDefinitions.get(79).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                5,
                dialogResponseDefinitions.get(80).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(81).getMessage(), 6),
                        new Response(dialogResponseDefinitions.get(82).getMessage(), Constants.Dialog.CLOSE_DIALOG_NO_CHANGE)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.ELDER),
                6,
                dialogResponseDefinitions.get(83).getMessage(),
                new Response[]{},
                5
        ));

        //Bartender
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BARTENDER),
                0,
                dialogResponseDefinitions.get(84).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(85).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(86).getMessage(), 1)}
        ));
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BARTENDER),
                1,
                dialogResponseDefinitions.get(87).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(88).getMessage(), 4),
                        new Response(dialogResponseDefinitions.get(89).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BARTENDER),
                3,
                dialogResponseDefinitions.get(90).getMessage(),
                new Response[]{},
                0
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BARTENDER),
                4,
                dialogResponseDefinitions.get(91).getMessage(),
                new Response[]{},
                0
        ));

        //Priest (in pub)
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.PRIEST),
                0,
                dialogResponseDefinitions.get(92).getMessage(),
                new Response[]{},
                0
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.PRIEST),
                1,
                dialogResponseDefinitions.get(93).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(94).getMessage(), 2),
                        new Response(dialogResponseDefinitions.get(95).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.PRIEST),
                2,
                dialogResponseDefinitions.get(96).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(97).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(98).getMessage(), -1)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.PRIEST),
                3,
                dialogResponseDefinitions.get(99).getMessage(),
                new Response[]{},
                0
        ));

        //BERGESS
        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BERGESS),
                0,
                dialogResponseDefinitions.get(100).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(101).getMessage(), 1),
                        new Response(dialogResponseDefinitions.get(102).getMessage(), 2)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.PRIEST),
                2,
                dialogResponseDefinitions.get(103).getMessage(),
                new Response[]{},
                0
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BERGESS),
                1,
                dialogResponseDefinitions.get(104).getMessage(),
                new Response[]{
                        new Response(dialogResponseDefinitions.get(105).getMessage(), 3),
                        new Response(dialogResponseDefinitions.get(106).getMessage(), 3)}
        ));

        prompts.add(new Prompt(
                entityDefinitions.get(Constants.Entities.BERGESS),
                5,
                dialogResponseDefinitions.get(107).getMessage(),
                new Response[]{},
                0
        ));

        setPromptDefinitions(prompts);
    }

    public ArrayList<Prompt> getPromptDefinitions() {
        return promptDefinitions;
    }

    public void setPromptDefinitions(ArrayList<Prompt> promptDefinitions) {
        this.promptDefinitions = promptDefinitions;
    }
}
