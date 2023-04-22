package com.ge.baseobject.entity;
import com.ge.baseobject.BaseObject;
import com.ge.baseobject.item.Weapon;
import com.ge.baseobject.room.Room;
import com.ge.baseobject.item.Item;
import com.ge.general.ApplicationWindow;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public abstract class Entity extends BaseObject {
    public PropertyChangeSupport changes = new PropertyChangeSupport(this);
    public PropertyChangeSupport changes2 = new PropertyChangeSupport(this);

    private boolean isAttackable;
    public boolean getIsAttackable(){

        return isAttackable;
    }
    public void setIsAttackable(boolean isAttackable){
        this.isAttackable = isAttackable;
    }

    private int dialogState;
    public int getDialogState(){
        return this.dialogState;
    }
    public void setDialogState(int dialogState){
        this.dialogState = dialogState;
    }


    public void addInventoryItem(Item item){
        if (getInventorySize() <= MAX_INVENTORY_SIZE){
            inventory.add(item);
        } else {
            ApplicationWindow.print(ApplicationWindow.getWorld().getGameMessageDefinitions().get(67).getMessage());
            getRoom().addItem(item);
        }
    }

    public void removeInventoryItem(Item item){
        inventory.remove(item);
    }

    private ArrayList<Item> inventory;
    public ArrayList<Item> getInventory(){
        return inventory;
    }
    public int getInventorySize() {
        return inventory.size();
    }
    public final int MAX_INVENTORY_SIZE = 15; //arbitrary

    public boolean hasInvetoryItemById(int itemId){
        for (Item item : getInventory()){
            if (item.getId() ==  itemId){
                return true;
            }
        }
        return false;
    }

    public boolean hasWeapon(){
        for (Item item : getInventory()){
            if (item instanceof Weapon){
                return true;
            }
        }
        return false;
    }

    private int moves;
    public int getMoves(){ return moves;}
    public void setMoves(int moves){
        this.moves = moves;
    }



    public abstract void death();

    public Entity(int id, String name, String examine, boolean isAttackable){
        this.setId(id);
        this.setName(name);
        this.setExamine(examine);
        this.setIsAttackable(isAttackable);
        inventory = new ArrayList<>();
    }

    @Override
    public void setRoom(Room room){
        changes2.firePropertyChange("room", getRoom(), room);
        super.setRoom(room);
    }
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
    public void addRoomChangeChangeListener(PropertyChangeListener l) {
        changes2.addPropertyChangeListener(l);
    }


}
