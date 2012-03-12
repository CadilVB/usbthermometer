/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual;

import USBThermometerLib.Device;
import Engine.Host;
import Engine.HostObserver;
import Main.Main;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JLabel;

/**
 *
 * @author pawelkn
 */
public class SensorStatusLabel extends JLabel implements HostObserver {

    private static SensorStatusLabel instance;
    private static ResourceBundle bundle;
    private Sensor sensor;

    private SensorStatusLabel() {
        super();

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());
    }

    public static synchronized SensorStatusLabel getInstance() {
        if( instance == null ) {
            instance = new SensorStatusLabel();
        }
        return instance;
    }
    
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        Main.getLocalhost().addHostObserver(instance);
        setText(sensor.getName() + " (" + sensor.getStringId() + ") " + sensor.getClass().getSimpleName());
    }   
    
    public void removeSensor() {
        if( sensor != null ) {
            Main.getLocalhost().removeHostObserver(instance);
        }
        setText("");
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
        setText(sensor.getName() + " (" + sensor.getStringId() + ") " + sensor.getClass().getSimpleName());
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceAdd(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
    }
}
