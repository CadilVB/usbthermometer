package Main;


import USBThermometerLib.Sensor;
import USBThermometerLib.Device;
import Tools.WorkspaceManager;
import Tools.ConfigurationManager;
import Tools.JustOne;
import Visual.*;
import Engine.*;
import Tools.Configuration;
import Tools.WinStartUp;
import Triggers.TriggersManager;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.D53A3757-1970-CC77-A97C-EE8A76B63376]
// </editor-fold> 
public class Main {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.C30D5138-1E91-FF5D-E823-289459B2A855]
    // </editor-fold> 
    private static Host localhost;
    private static TriggersManager triggersManager;

    private static final DateFormat df = new SimpleDateFormat("yy.MM.dd.HH.mm.ss");

    private static String exePath;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.95D79ED9-8065-FDB3-FA43-9D582B7454C9]
    // </editor-fold> 
    public static void main (String args[]) throws IOException {

        Date date = new Date();
        new File("./log").mkdirs();
        Handler fh = new FileHandler("./log/USBThermometer." + df.format(date) + ".log");
        Logger.getLogger("USBThermometer").addHandler(fh);
        Logger.getLogger("USBThermometer").setLevel(Level.ALL);

        System.out.println("USBThermometerApp...");

        if(args.length > 0) {
            exePath = args[0];
        }

        final Configuration c = ConfigurationManager.load();

        if( c.isLockMultipleStartsUp() ) {
            JustOne.check();
        }

        if( c.isStartupOnWindowsStartUp() ) {
            WinStartUp.set(exePath);
        } else {
            WinStartUp.clear();
        }               

        if( c.isUseProxy() ) {
            System.getProperties().put( "proxySet", "true" );
            System.getProperties().put( "proxyHost", c.getProxyHost() );
            System.getProperties().put( "proxyPort", c.getProxyPort() );
        } else {
            System.getProperties().put( "proxySet", "false" );
        }

        Database database = c.getDatabase();
        if(database == null) {
            database = new SQLite("USBThermometer.db");
            c.setDatabase(database);
            ConfigurationManager.save(c);
        }

        database.initialize();
        localhost = new Host(database);
        localhost.setSamplingRate(c.getSamplingRate());
        localhost.startOperation();

        triggersManager = new TriggersManager(localhost);        

        if( c.isGuiMode() ) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainForm.getInstance();
                    ConfigurationManager.notiffyConfigurationObservers(c);
                }
            });
        }
    }

    public static Host getLocalhost() {
        return localhost;
    }

    public static TriggersManager getTriggerManager() {
        return triggersManager;
    }

    public static String getExePath() {
        return exePath;
    }

    public static Sensor findSensorByName(String name) {
        for (Device device : localhost.getDevices()) {
            for (Sensor sensor : device.getSensors()) {
                if (sensor.getName().equals(name)) {
                    return sensor;
                }
            }
        }
        return null;
    }

    public static Sensor findSensorByStringId(String id) {
        for (Device device : localhost.getDevices()) {
            for (Sensor sensor : device.getSensors()) {
                if (sensor.getStringId().equals(id)) {
                    return sensor;
                }
            }
        }
        return null;
    }

    public static void exit() {
        WorkspaceManager.save();
        System.exit(0);
    }
}

