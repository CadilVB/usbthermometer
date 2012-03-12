/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pawelkn
 */
public class TCPIPTrigger extends EthernetTrigger {

    public TCPIPTrigger() {
        super();

        setName("TCP/IP Trigger");
    }

    @Override
    public void onEnter() {
        try {
            Socket socket = new Socket(getHost(), getPort());
            OutputStream os = socket.getOutputStream();
            os.write(getCommand());
            os.flush();
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }
}
