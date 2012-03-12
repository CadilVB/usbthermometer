/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual;

import Main.Main;
import USBThermometerLib.Device;
import USBThermometerLib.DeviceState;
import Engine.Host;
import Engine.HostObserver;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import java.awt.Dimension;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 *
 * @author Paweł
 */
public class SearchProgressBar extends JProgressBar implements HostObserver, Runnable {
    private static ResourceBundle bundle;

    public SearchProgressBar() {
        super();

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        this.setMaximumSize(new Dimension(146,14));
        this.setStringPainted(true);

        Main.getLocalhost().addHostObserver(this);

        if( !isSearchDone() ) {
            this.setString(bundle.getString("SEARCH_SENSORS..."));
            threadRun = true;
            th = new Thread(this);
            th.start();
        }
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
    }

    @Override
    public synchronized void notiffyDeviceAdd(Host host, Device device) {
        if( !threadRun ) {
            this.setString(bundle.getString("SEARCH_SENSORS..."));
            threadRun = true;
            th = new Thread(this);
            th.start();
        }
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
    }

    @Override
    public void run() {
        while(threadRun) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }

            progress++;
            if( progress > this.getMaximum() ) {
                progress = 0;
            }
            this.setValue(progress);

            if( isSearchDone() ) {
                this.setString(bundle.getString("RUNNING..."));
                threadRun = false;
            }          
        }
        this.setValue(3);
    }

    private boolean isSearchDone() {
        boolean searchDone = true;
        for (Device d : Main.getLocalhost().getDevices()) {
            if (d.getState() == DeviceState.SEARCHSENSORS) {
                searchDone = false;
            }
        }
        return searchDone;
    }

    private Thread th;
    private boolean threadRun;
    private int progress;
}
