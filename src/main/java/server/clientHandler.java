/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ConnectionInfo;
import model.User;

/**
 *
 * @author TUAN
 */
public class clientHandler extends Thread{
    private Socket socket;
    private int count;
    
    public clientHandler(Socket socket, int count){
        this.socket = socket;
        this.count = count;
    }
    
    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            InetAddress address = socket.getInetAddress();
            int port = socket.getPort();
            String hostName = address.getHostName();
            String hostAddress = address.getHostAddress();
            User user = (User) ois.readObject();
            if(user.getAction().equals("register")){
                String res = checkRegister(user.getUserName(), user.getFullName(), user.getPassword());
                oos.writeObject(res);
                oos.flush();
            }
            else if(user.getAction().equals("login")){
                boolean res = checkLogin(user.getUserName(), user.getPassword());
                oos.writeBoolean(res);
                oos.flush();
                if(res){
                    ConnectionInfo info = new ConnectionInfo(count, hostName, hostAddress, port);
                    oos.writeObject(info);
                    oos.flush();
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(clientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(clientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String checkRegister(String userName, String fullName, String password) {
    String url = "jdbc:mysql://localhost:3306/log_res";
    String dbUser = "tuanduog";
    String dbPass = "abc123!@#";

    try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblusers WHERE UserName = ?");
        stmt.setString(1, userName);
        ResultSet res = stmt.executeQuery();
        if (res.next()) {
            return "Account exists";
        }

        PreparedStatement stmt2 = conn.prepareStatement(
            "INSERT INTO log_res.tblusers (FullName, UserName, Password) VALUES (?, ?, ?)"
        );
        stmt2.setString(1, fullName);
        stmt2.setString(2, userName);
        stmt2.setString(3, password);

        int inserted = stmt2.executeUpdate();
        if (inserted > 0) {
            return "Add ok";
        } else {
            return "Add false";
        }
    } catch (SQLException ex) {
        Logger.getLogger(clientHandler.class.getName()).log(Level.SEVERE, null, ex);
        return "Register failed: " + ex.getMessage();  // Trả về chi tiết lỗi
    }
}
    public boolean checkLogin(String userName, String password){
        String url = "jdbc:mysql://localhost:3306/log_res";
        String dbUser = "tuanduog";
        String dbPass = "abc123!@#";
        
        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblusers WHERE UserName = ? AND Password = ?");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            ResultSet res = stmt.executeQuery();
            return res.next();
        } catch (SQLException ex) {
            Logger.getLogger(clientHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }

}
