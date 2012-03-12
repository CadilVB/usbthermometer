/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Visual.Triggers.EthernetTriggerConfigurationPanel;
import javax.swing.JPanel;

/**
 *
 * @author pawelkn
 */
public abstract class EthernetTrigger extends Trigger {

    private EthernetTriggerConfigurationPanel c;

    private String host = "localhost";
    private int port = 12345;
    private byte[] command;

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public JPanel getConfigurationPanel() {
        c = new EthernetTriggerConfigurationPanel();
        c.setHost(host);
        c.setPort(port);
        c.setCommand(command);
        return c;
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onExit() {
    }

    @Override
    public void updateConfiguration() {
        if( c != null ) {
            host = c.getHost();
            port = c.getPort();
            command = c.getCommand();
        }
    }

}
