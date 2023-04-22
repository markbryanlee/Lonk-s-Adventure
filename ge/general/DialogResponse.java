package com.ge.general;

public class DialogResponse implements java.io.Serializable{
    private int id;
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    private String message;
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

    public DialogResponse(int id, String message){
        this.setId(id);
        this.setMessage(message);
    }
}
