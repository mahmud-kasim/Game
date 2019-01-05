
package com.skdev.classes;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JPanel;


public class Message implements Serializable {
    public int code, clickedLocation;
    String message;
    ArrayList<BSButton> buttons = new ArrayList<BSButton>();    
    
    public Message() {
    }

    public Message(int code, int clickedLocation) {
        this.code = code;
        this.clickedLocation = clickedLocation;
    }
    
    public Message(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public Message(int code, ArrayList<BSButton> buttons) {
        this.code = code;
        this.buttons = buttons;
    }

    public Message(int code) {
        this.code = code;
    }

    public Message(String message) {
        this.message = message;
    }

    public ArrayList<BSButton> getButtons() {
        return buttons;
    }

    public int getClickedLocation() {
        return clickedLocation;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
