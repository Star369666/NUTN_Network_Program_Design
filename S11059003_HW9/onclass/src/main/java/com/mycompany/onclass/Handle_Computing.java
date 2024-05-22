/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.onclass;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author USER
 */
public class Handle_Computing extends Thread {
    private Socket socket;
    private RunServer server;
    
    public Handle_Computing(Socket socket, RunServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    public void run() {
        Scanner input = null;
        PrintWriter output = null;
        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            String msg;
            do {
                msg = input.nextLine();
                server.getWindow().text.append("Client sent: " + msg + "\n");
                output.println("Echo back your message: " + msg);
            } while(!msg.equals("CLOSE"));
            server.getWindow().text.append("Client disconnected.\n");
        }
        catch(IOException e) {
            Logger.getLogger(Handle_Computing.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}