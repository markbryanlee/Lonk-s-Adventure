package com.ge.action;

import java.util.HashSet;

public abstract class Action  implements java.io.Serializable{
    private HashSet<String> verbs;
    public HashSet<String> getVerbs(){
        return this.verbs;
    }
    public void setVerbs(HashSet<String> verbs){
        this.verbs = verbs;
    }


    private String verb;
    public void setVerb(String verb){
        this.verb = verb;
    }
    public String getVerb(){
        return this.verb;
    }
    private String subject;
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return this.subject;
    }

    private String description;
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public abstract String execute();

    public Action(){

    }

    public String toString(){
        return getDescription();
    }
}
