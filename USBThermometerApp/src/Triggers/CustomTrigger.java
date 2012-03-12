/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Main.Main;
import Visual.Triggers.CustomTriggerConfigurationPanel;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author pawelkn
 */
public class CustomTrigger extends Trigger {

    private CustomTriggerConfigurationPanel c;

    private String command;

    public CustomTrigger() {
        super();

        setName("User Trigger");
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    
    @Override
    public JPanel getConfigurationPanel() {
        c = new CustomTriggerConfigurationPanel();
        c.setJarFilePath(command);
        return c;
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onEnter() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process pr = rt.exec(command);
                    pr.waitFor();
                } catch (Exception ex) {
                    Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                } 
            }
        }).start();
    }

    @Override
    public void onExit() {
    }

    @Override
    public void updateConfiguration() {
        if( c != null ) {
            command = c.getJarFilePath();
        }
    }

}
