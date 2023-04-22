package com.ge.baseobject.gameobject;

import com.ge.baseobject.BaseObject;

public abstract class GameObject extends BaseObject{
    public GameObject(int id, String name, String examine, boolean isDestructible, boolean isInteractable) {
        this.setId(id);
        this.setName(name);
        this.setExamine(examine);
        this.setIsDestructible(isDestructible);
        this.setIsInteractable(isInteractable);

    }
    private boolean isDestructible;
    public void setIsDestructible(boolean isDestructible){
        this.isDestructible = isDestructible;
    }
    private boolean isInteractable;
    public void setIsInteractable(boolean isInteractable){
        this.isInteractable = isInteractable;
    }
    public boolean getIsInteractable(){
        return this.isInteractable;
    }
}
