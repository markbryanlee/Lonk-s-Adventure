package com.ge.baseobject;

import com.ge.baseobject.room.Room;
import com.ge.general.World;

public abstract class BaseObject implements java.io.Serializable{
    //base objects are : Rooms, Entities, Items, GameObjects

    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    private String name;
    public String getName(){

        return name;
    }
    public void setName(String name){

        this.name = name;
    }
    private String examine;
    public String getExamine(){

        return examine;
    }
    public void setExamine(String examine){
        this.examine = examine;
    }

    private Room room;
    public Room getRoom(){
        return room;
    }
    public void setRoom(Room room){
        this.room = room;
    }

    public abstract String interact(World world);

    public String toString(){
        return getName();
    }


}
