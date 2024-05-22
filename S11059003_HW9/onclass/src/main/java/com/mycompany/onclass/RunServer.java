/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onclass;
import java.io.*;
import java.net.*;
import java.util.logging.*;
/**
 *
 * @author USER
 */
public class RunServer extends Thread {
    private ServerWin window;
    private ServerSocket server_socket;
    private int port;
    public RunServer(int port, ServerWin win) throws IOException {
        this.port = port;
        this.window = win;
        server_socket = new ServerSocket(port);
    }
    
    public void run() {
        Socket socket;
        window.text.setText("Server is running\n");
        while(true) {
            try {
                socket = server_socket.accept();
                window.text.append("Client connected.\n");
                new Handle_Computing(socket, this).start();
            }
            catch(IOException e) {
                Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    public ServerWin getWindow() {
        return this.window;
    }
}