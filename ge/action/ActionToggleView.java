package com.ge.action;

import com.ge.general.ApplicationWindow;

public class ActionToggleView extends Action{
    @Override
    public String execute() {
        ApplicationWindow.toggleView();  //debug purposes only
        return "Toggled view.";
    }
}
