/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

/**
 *
 * @author pawelkn
 */
import Visual.MainForm;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class JustOneServer extends Thread {
    // you may need to customize this for your machine

    public static final int PORT = 45841;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;

    public void run() {
        while (true) {
            try {
                // Create the server socket
                serverSocket = new ServerSocket(PORT, 1);
                while (true) {
                    // Wait for a connection
                    clientSocket = serverSocket.accept();
                    // System.out.println("*** Got a connection! ");
                    MainForm.getInstance().setVisible(true);
                    MainForm.getInstance().setState(JFrame.NORMAL);
                    MainForm.getInstance().setAlwaysOnTop(true);
                    MainForm.getInstance().setAlwaysOnTop(false);

                    clientSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }
}
