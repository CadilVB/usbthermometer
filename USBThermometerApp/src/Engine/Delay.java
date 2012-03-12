/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Engine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paweł
 */
public class Delay {

    public static void Ms(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

}
