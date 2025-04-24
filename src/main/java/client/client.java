/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConnectionInfo;
import model.User;

/**
 *
 * @author TUAN
 */
public class client {
        int port = 1222;
        String address = "localhost";
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
    public String register(User user) throws ClassNotFoundException {
        if (user == null || user.getUserName() == null || user.getFullName() == null || user.getPassword() == null) {
            return "Invalid user data";
        }
        
        String result = "Add false"; // mặc định là lỗi
    
        try {
            socket = new Socket(address, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            user.setAction("register");
            // Gửi user object tới server
            oos.writeObject(user);
            oos.flush();
            // Nhận kết quả từ server
            result = (String) ois.readObject();
    
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
        return result;
    }
    public Object[] login(User user) throws ClassNotFoundException{
        Object[] result = new Object[3]; // [0] boolean  [1] connectioninfo
            try {
                socket = new Socket(address, port);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                user.setAction("login");
                oos.writeObject(user);
                oos.flush();
                
                boolean loginSuccess = ois.readBoolean();
                result[0] = loginSuccess;
                
                if(loginSuccess){
                    ConnectionInfo conn = (ConnectionInfo) ois.readObject();
                    result[1] = conn;
                    result[2] = user;
                }
            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(ois != null) ois.close();
                    if(oos != null) oos.close();
                    if(socket != null) socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        return result;
    }
    
}
