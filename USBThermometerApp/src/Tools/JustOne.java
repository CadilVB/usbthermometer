/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

/**
 *
 * @author pawelkn
 */
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JustOne {

    private static JustOneServer sds = null;

    public static void check() {
        delayRandom();
        try {
            new Socket("localhost", JustOneServer.PORT);
            System.out.println("*** Already running!");
            System.exit(1);
        } catch (Exception e) {
            sds = new JustOneServer();
            sds.start();
        }
    }

    private static void delayRandom() {
        Random r = new Random();
        try {
            Thread.sleep(r.nextInt(100) + 1);
        } catch (InterruptedException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }
}
