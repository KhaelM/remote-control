package com.itu.client;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.itu.robot.RobotAction;
import com.itu.server.Server;;

/**
 * Client
 */
public class Client {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Robot robot;
    private Socket socket;

    public Client(String serverMachine, String studentName) throws IOException, AWTException {
        socket = new Socket(serverMachine, Server.PORT);
        robot = new Robot();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        out.writeObject(studentName);
        out.flush();
    }

    public void run() {
        try {
            while (true) {
                RobotAction action = (RobotAction) in.readObject();
                Object result = action.execute(robot);

                // if it's a screenshot
                if(result != null) {
                    out.writeObject(result);
                    out.flush();
                    out.reset();
                }
            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            System.out.println("Connection ferme. cause: ");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(args[0], args[1]);
            client.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}