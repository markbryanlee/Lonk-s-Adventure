package com.ge.baseobject.room;
import com.ge.baseobject.BaseObject;
import com.ge.baseobject.entity.Entity;
import com.ge.baseobject.gameobject.GameObject;
import com.ge.baseobject.item.Item;
import com.ge.general.ApplicationWindow;
import com.ge.general.World;

import java.util.*;

public class Room extends BaseObject {
    //Room is a container for:
    //Entity (Npc/Player)
    //Item
    //Game Object

    //public PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private boolean isLocked;
    public boolean getIsLocked(){
        return this.isLocked;
    }
    public void setIsLocked(boolean isLocked){
        this.isLocked = isLocked;
    }

    public static final int NEIGHBOUR_NONE = 0;
    private int neighbourNorth;
    public void setNeighbourNorth(int neighNorth){
        this.neighbourNorth = neighNorth;
    }
    public int getNeighbourNorth(){
        return this.neighbourNorth;
    }
    private int neighbourSouth;
    public void setNeighbourSouth(int neighSouth){
        this.neighbourSouth = neighSouth;
    }
    public int getNeighbourSouth(){
        return this.neighbourSouth;
    }
    private int neighbourEast;
    public void setNeighbourEast(int neighEast){
        this.neighbourEast = neighEast;
    }
    public int getNeighbourEast(){
        return this.neighbourEast;
    }
    private int neighbourWest;
    public void setNeighbourWest(int neighWest){
        this.neighbourWest = neighWest;
    }
    public int getNeighbourWest(){
        return this.neighbourWest;
    }


    private final ArrayList<Entity> entities;
    private final ArrayList<Item> items;
    private final ArrayList<GameObject> gameObjects;

    public Room(int id, String name, String examine, boolean isLocked, int neighNorth, int neighSouth, int neighEast, int neighWest){
        this.setId(id);
        this.setName(name);
        this.setExamine(examine);
        this.setIsLocked(isLocked);
        this.setNeighbourNorth(neighNorth);
        this.setNeighbourSouth(neighSouth);
        this.setNeighbourWest(neighWest);
        this.setNeighbourEast(neighEast);

        entities = new ArrayList<>();
        items = new ArrayList<>();
        gameObjects = new ArrayList<>();
    }

    public void addEntity(Entity entity)
    {
        entity.setRoom(this);
        entities.add(entity);
    }
    public void removeEntity(Entity entity)
    {
        entities.remove(entity);
    }

    public void addItem(Item item)
    {
        items.add(item);
    }
    public void removeItem(Item item)
    {
        items.remove(item);
    }
    public void addGameObject(GameObject gameObject)
    {
        gameObject.setRoom(this);
        gameObjects.add(gameObject);
    }
    public void removeGameObject(GameObject gameObject)
    {
        gameObjects.remove(gameObject);
    }
    public void clear(){
        for (Entity entity : getEntities()){
            removeEntity(entity);
        }
        for (Item item : getItems()){
            removeItem(item);
        }
        for (GameObject go : getGameObjects()){
            removeGameObject(go);
        }
    }
    public ArrayList<Entity> getEntities(){
        return entities;
    }
    public ArrayList<Item> getItems(){
        return items;
    }
    public ArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }

    @Override
    public String interact(World world) {
        return "Nothing interesting happens...";
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        //sb.append("Room Id:").append(getId()).append("\n");
        sb.append("You are in ").append(getName()).append("\n");
        //sb.append(getExamine()).append("\n");

        sb.append("NPCs: ");
        StringJoiner sj = new StringJoiner(", ");

        for (Entity e: getEntities())
        {
            sj.add(e.getName() + " (" + e.getDialogState() + ")");
        }

        if (sj.length() == 0)
        {
            sb.append("None\n");
        } else {
            sb.append(sj).append("\n");
        }

        sj = new StringJoiner(", ");
        sb.append("Items: ");
        for (Item i: getItems())
        {
            sj.add(i.getName());
        }

        if (sj.length() == 0)
        {
            sb.append("None\n");
        } else {
            sb.append(sj).append("\n");
        }

        sj = new StringJoiner(", ");
        sb.append("GameObjects: ");
        for (GameObject o: getGameObjects())
        {
            sj.add(o.getName());
        }

        if (sj.length() == 0)
        {
            sb.append("None\n");
        } else {
            sb.append(sj).append("\n");
        }


        return sb.toString();
    }

    public void lockSurroundingExits(boolean lockNorth, boolean lockSouth, boolean lockEast, boolean lockWest){
        if (lockNorth && getNeighbourNorth() != NEIGHBOUR_NONE){
            Room room1 = ApplicationWindow.getWorld().getRoomById(getNeighbourNorth());
            room1.setIsLocked(true);
        }

        if (lockSouth && getNeighbourSouth() != NEIGHBOUR_NONE){
            Room room2 = ApplicationWindow.getWorld().getRoomById(getNeighbourSouth());
            room2.setIsLocked(true);
        }

        if (lockEast && getNeighbourEast() != NEIGHBOUR_NONE){
            Room room3 = ApplicationWindow.getWorld().getRoomById(getNeighbourEast());
            room3.setIsLocked(true);
        }

        if (lockWest && getNeighbourWest() != NEIGHBOUR_NONE){
            Room room4 = ApplicationWindow.getWorld().getRoomById(getNeighbourWest());
            room4.setIsLocked(true);
        }
    }

    public void unlockSurroundingExits(boolean lockNorth, boolean lockSouth, boolean lockEast, boolean lockWest){
        if (lockNorth && getNeighbourNorth() != NEIGHBOUR_NONE){
            Room room1 = ApplicationWindow.getWorld().getRoomById(getNeighbourNorth());
            room1.setIsLocked(false);
        }

        if (lockSouth && getNeighbourSouth() != NEIGHBOUR_NONE){
            Room room2 = ApplicationWindow.getWorld().getRoomById(getNeighbourSouth());
            room2.setIsLocked(false);
        }

        if (lockEast && getNeighbourEast() != NEIGHBOUR_NONE){
            Room room3 = ApplicationWindow.getWorld().getRoomById(getNeighbourEast());
            room3.setIsLocked(false);
        }

        if (lockWest && getNeighbourWest() != NEIGHBOUR_NONE){
            Room room4 = ApplicationWindow.getWorld().getRoomById(getNeighbourWest());
            room4.setIsLocked(false);
        }
    }

    public void killNpc(Entity entity){
        ApplicationWindow.getWorld().getChanges().firePropertyChange(String.format("Npc dead -> %s ", entity.getName()), "old", "new");
        removeEntity(entity);


        //TODO: SEE IF THIS CAN BE REFACTORED TO A SEPARATE CLASS
        if (getName().equalsIgnoreCase("Lost Woods")){
            //challenge completed
            unlockSurroundingExits(true,true,true,true);
        }

    }
    public void spawnNpc(Entity entity){
        ApplicationWindow.getWorld().getChanges().firePropertyChange(String.format("Npc Respawn -> %s ", entity.getName()), "old", "new");
        addEntity(entity);
    }
}
