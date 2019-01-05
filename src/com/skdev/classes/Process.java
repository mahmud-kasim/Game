

package com.skdev.classes;

import java.io.IOException;


public interface Process {
    
    public void send(int code, String msg) throws Exception;
    
    public void sendChat(int code, String msg) throws Exception;
    
    public Message receive() throws IOException, ClassNotFoundException;
    
}
