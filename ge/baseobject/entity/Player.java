package com.ge.baseobject.entity;
import com.ge.util.Constants;
import com.ge.baseobject.room.Room;
import com.ge.general.ApplicationWindow;
import com.ge.general.World;

public class Player extends Entity implements java.io.Serializable{
    @Override
    public void death() {
        World world = ApplicationWindow.getWorld();
        this.getRoom().removeEntity(this);

        //respawn player
        this.setRoom(world.getRoomById(Constants.Rooms.LONKS_HOUSE));
        world.getChanges().firePropertyChange("Player Respawned", "old", "new");
    }

    public Player(int id, String name, String examine, boolean isAttackable) {
        super(id, name, examine, isAttackable);
    }

    @Override
    public void setRoom(Room room){
        super.setRoom(room);
    }

    @Override
    public String interact(World world) {
        return "Nothing interesting happens...";
    }
}
