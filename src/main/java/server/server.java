/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TUAN
 */
public class server {
    public static void main(String[] args){
        int port = 1222;
        ServerSocket server = null;
        Socket client = null;
        int count;
        try {
            server = new ServerSocket(port);
            System.out.println("Server is ready...");
            count = 0;
            while(true){
                client = server.accept();
                new clientHandler(client, count).start();
                count++;
            }
        } catch (IOException ex) {
            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
