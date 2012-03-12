/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tools;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paweł
 */
public class WinStartUp {

    private static final int HKEY = WinRegistry.HKEY_CURRENT_USER;
    private static final String KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String VALUE = "USBThermometer";

    public static boolean set(String exePath) {
        if ((System.getProperty("os.name").toLowerCase().contains("win")) && (exePath != null))  {
            try {
                WinRegistry.writeStringValue(HKEY, KEY, VALUE, exePath);
                return true;
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean clear() {
        if (System.getProperty("os.name").toLowerCase().contains("win"))  {
            try {
                WinRegistry.deleteValue(HKEY, KEY, VALUE);
                return true;
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }
}