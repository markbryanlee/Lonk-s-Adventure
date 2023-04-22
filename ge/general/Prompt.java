package com.ge.general;

import com.ge.baseobject.entity.Entity;

public class Prompt  implements java.io.Serializable{

    private Entity entity;
    private String message;
    private Response[] responses;
    private int dialogState;
    private int defaultNextState = -1;

    public Prompt(Entity entity, int dialogState, String message, Response[] responses){
        this.setEntity(entity);
        this.setDialogState(dialogState);
        this.setMessage(message);
        this.setResponses(responses);
    }

    public Prompt(Entity entity, int dialogState, String message, Response[] responses, int defaultNextState){
        this.setEntity(entity);
        this.setDialogState(dialogState);
        this.setMessage(message);
        this.setResponses(responses);
        this.setDefaultNextState(defaultNextState);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response[] getResponses() {
        return responses;
    }

    public void setResponses(Response[] responses) {
        this.responses = responses;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public int getDialogState() {
        return dialogState;
    }

    public void setDialogState(int dialogState) {
        this.dialogState = dialogState;
    }

    public int getDefaultNextState() {
        return defaultNextState;
    }

    public void setDefaultNextState(int defaultNextState) {
        this.defaultNextState = defaultNextState;
    }
}
