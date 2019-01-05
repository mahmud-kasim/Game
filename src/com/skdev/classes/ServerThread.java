

package com.skdev.classes;

import com.skdev.GUI.MainWindow;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JPanel;


public class ServerThread extends Thread implements Process {
    private Socket connection;
    private int port;
    private MainWindow father;
    private ServerSocket server;
    private ObjectOutputStream output, chatOutput;
    private ObjectInputStream input;
    private Message message;
    private ChatReceive chat;
    private ArrayList<User> users;
    
    public ServerThread(int port, MainWindow father) throws IOException {
        this.port = port;
        this.father = father;
        server = new ServerSocket(port);
        this.message = new Message();
        this.users = new ArrayList<User>();
    }

    @Override
    public void send(int code, String msg) throws Exception {
        Message message = new Message(code, msg);
        output.writeObject(message);
    }
    
    // Sends the buttons
    public void send(int code, ArrayList<BSButton> buttons) throws Exception {
        Message message = new Message(code, buttons);
        output.writeObject(message);
    }
    // Sends the clickedCell
    public void send(int code, int clickedLocation) throws Exception {
        Message message = new Message(code, clickedLocation);
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
            connection = server.accept();
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
            
            //  Cria usuario 1;
            User u1 = new User(father.getPlayerName());
            users.add(u1);
            
            //  Handshaking. Enviar nick do jogador.
            this.send(100, users.get(0).getName());

            //  Passa referência deste processo para o processo pai.
            father.setProc((Process)this);
            
            boolean flag = true;
            do {
                flag = checkMessage(receive());
            } while (flag);

        } catch (Exception ex) {
            father.message("error");
            System.err.println("Sockets error: " + ex.toString());
        }
   }
   
   public boolean checkMessage(Message message) throws Exception {
       int code = message.getCode();
       switch(code) {
           case 100:
           User u2 = new User(message.getMessage());
           users.add(u2);
           father.sendChat(u2.getName() + " deneme.");
           father.startNetworkGame();
           father.getpOneGrid().enableCells();
           father.getpOneGrid().placeShips();
           while(!father.getpOneGrid().areShipsPlaced()){System.out.print("");}
           send(101, "");
           return true;
           case 102:
           father.getpTwoGrid().setButtons(message.getButtons());
           father.getpTwoGrid().enableCells();
           father.getpTwoGrid().addMyCellListener();
           send(103, father.getpOneGrid().getButtons());
           return true;
           case 104:
           father.getpOneGrid().setClicked(false);
           while(father.getpTwoGrid().getClicked() == false){System.out.print("");} // While not clicked
           send(105, father.getpTwoGrid().getClickedLocation());          
           return true;
           case 106:
           father.getpTwoGrid().setClicked(false);
           father.getpOneGrid().setClickedButton(message.getClickedLocation());
           while(father.getpTwoGrid().getClicked() == false){System.out.print("");} // While not clicked
           send(105, father.getpTwoGrid().getClickedLocation());     
           return true;
           case 1: 
           father.sendChat(message.getMessage());
           return true;     
           case 99: // End Game
           father.getpOneGrid().setEndGame();
           father.getpTwoGrid().setEndGame();
           father.getpOneGrid().endPlayerGame();
           break;
           default:
           break;
   }
       return true;
   }
}
