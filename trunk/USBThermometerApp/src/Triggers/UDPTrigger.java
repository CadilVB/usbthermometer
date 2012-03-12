/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pawelkn
 */
public class UDPTrigger extends EthernetTrigger {

    public UDPTrigger() {
        super();

        setName("UDP Trigger");
    }

    @Override
    public void onEnter() {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] command = getCommand();
            InetAddress address = InetAddress.getByName(getHost());
            DatagramPacket packet = new DatagramPacket
                        (command, command.length,
                        address, getPort());
            socket.send(packet);
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }
}
