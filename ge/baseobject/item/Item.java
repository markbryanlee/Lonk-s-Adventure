package com.ge.baseobject.item;

import com.ge.baseobject.BaseObject;
import com.ge.general.World;

public abstract class Item extends BaseObject {

    @Override
    public String interact(World world) {
        return "Nothing interesting happens...";
    }

    public Item(int id, String name, String examine){
        this.setId(id);
        this.setName(name);
        this.setExamine(examine);
    }
}
