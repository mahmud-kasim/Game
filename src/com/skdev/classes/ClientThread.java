
package com.skdev.classes;

import com.skdev.GUI.MainWindow;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread implements Process {

    MainWindow father;
    String serverIP, name;
    int port;
    ObjectOutputStream output, chatOutput;
    ObjectInputStream input;
    Socket connection;

    public void sendString(String msg) {
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            System.err.println("err " + e.toString());
        }
    }
    public ClientThread(MainWindow father, String name, String serverIP, int port) {
        this.father = father;
        this.name = name;
        this.serverIP = serverIP;
        this.port = port;
    }
    // Sends the clickedCell
    public void send(int code, int clickedLocation) throws Exception {
        Message message = new Message(code, clickedLocation);
        output.writeObject(message);
    }
    @Override
    public void send(int code, String msg) throws Exception {
        Message message = new Message(code, msg);
        output.writeObject(message);
    }
    
    public void send(int code, ArrayList<BSButton> buttons) throws Exception {
        Message message = new Message(code, buttons);
        output.writeObject(message);
    }

    @Override
    public void sendChat(int code, String msg) throws Exception {
        Message message = new Message(code, msg);
        chatOutput.writeObject(message);
    }

    @Override
    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) input.readObject();
    }
    
    @Override
    public void run() {
        try {
            connection = new Socket(serverIP, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
            
            send(100, name);
            
            father.setProc((Process)this);
            
            father.message("DDD!");
            
            boolean flag = true;
            do {
                checkMessage(receive());
            } while(flag);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
    
       public boolean checkMessage(Message message) throws Exception {
       int code = message.getCode();
       switch(code) {
           case 100:
           father.sendChat(message.getMessage() + " rrr.");
           return true;
           case 101:
           father.startNetworkGame();
           father.getpTwoGrid().enableCells();
           father.getpTwoGrid().placeShips();
           while(!father.getpTwoGrid().areShipsPlaced()){System.out.print("");}
           send(102, father.getpTwoGrid().getButtons());
           return true;
           case 103:
           father.getpOneGrid().setButtons(message.getButtons());
           father.getpOneGrid().enableCells();
           father.getpOneGrid().addMyCellListener();
           father.getpOneGrid().setClicked(true);
           send(104, ""); // starts the game
           return true;
           case 105:
           father.getpOneGrid().setClicked(false);
           father.getpTwoGrid().setClickedButton(message.getClickedLocation());
           while(father.getpOneGrid().getClicked() == false){System.out.print("");}
           send(106, father.getpOneGrid().getClickedLocation());
           return true;
           case 1:
           father.sendChat(message.getMessage());
           return true;
           case 99: // End Game
           father.getpOneGrid().setEndGame();
           father.getpTwoGrid().setEndGame();
           father.getpOneGrid().endPlayerGame();
           return true;
           default:
           break;
       }
       return true;    
   } 
}
