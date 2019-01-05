
package com.skdev.classes;

import com.skdev.GUI.MainWindow;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


class ChatReceive extends Thread {
    private int port;
    private ServerSocket server;
    private Socket connection;
    private ObjectInputStream input;
    private MainWindow father;
    private boolean flag;

    public ChatReceive(int port, MainWindow father) throws IOException {
        this.port = port;
        this.father = father;
        flag = true;
        this.port = port;
        server = new ServerSocket(port+1);
    }
    
    @Override
    public void run() {
        try {
            connection = server.accept();
            input = new ObjectInputStream(connection.getInputStream());
            Message msg = new Message();
            
            do {
                try {
                    msg = (Message) input.readObject();
                } catch (Exception e) {
                    Logger.getLogger(ChatReceive.class.getName()).log(Level.SEVERE, null, e);
                }
                if(msg.getCode() == 1)
                    father.sendChat(msg.getMessage());
            } while (flag);
        } catch (IOException ioe) {
            Logger.getLogger(ChatReceive.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }
}
