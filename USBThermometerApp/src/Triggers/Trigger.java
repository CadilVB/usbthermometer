/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.Device;
import Engine.*;
import Main.Main;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 *
 * @author pawelkn
 */
public abstract class Trigger implements Serializable, HostObserver {

    public static final int STATE_STOP = 1;
    public static final int STATE_IDLE = 2;
    public static final int STATE_ACTIVE = 3;

    public static final int TRESHOLD_BELLOW = 0;
    public static final int TRESHOLD_OVER = 1;

    private int state = STATE_STOP;

    private boolean enabled = false;
    private String sensorId;
    private String name;
    private int thresholdDirection = TRESHOLD_BELLOW;
    private double threshold = 20.0;
    private double hysteresis = 0.1;

    private TriggersManager triggersManager;

    public double getHysteresis() {
        return hysteresis;
    }

    public void setHysteresis(double histeresis) {
        hysteresis = Math.abs(histeresis);
        hysteresis = ( hysteresis < 0.1 ? 0.1 : hysteresis );
        this.hysteresis = histeresis;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public int getThresholdDirection() {
        return thresholdDirection;
    }

    public void setThresholdDirection(int treshold) {
        this.thresholdDirection = treshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double value) {
        this.threshold = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if( !enabled ) {
            state = STATE_STOP;
        }
    }

    public TriggersManager getTriggersManager() {
        return triggersManager;
    }

    public void setTriggersManager(TriggersManager triggersManager) {
        this.triggersManager = triggersManager;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract JPanel getConfigurationPanel();

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
        if( enabled && (sample != null) ) {
            if( Main.findSensorByStringId(sensorId) == sample.getSensor() ) {
                onChange();

                switch( state ) {
                    case STATE_STOP:
                        switch( thresholdDirection ) {
                            case TRESHOLD_OVER:
                                if( sample.getValue() + hysteresis <= threshold ){
                                    state = STATE_IDLE;
                                }
                                break;
                            case TRESHOLD_BELLOW:
                                if( sample.getValue() - hysteresis >= threshold ){
                                    state = STATE_IDLE;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case STATE_IDLE:
                        switch( thresholdDirection ) {
                            case TRESHOLD_OVER:
                                if( sample.getValue() > threshold ){
                                    state = STATE_ACTIVE;
                                    onEnter();
                                }
                                break;
                            case TRESHOLD_BELLOW:
                                if( sample.getValue() < threshold ){
                                    state = STATE_ACTIVE;
                                    onEnter();
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case STATE_ACTIVE:
                        switch( thresholdDirection ) {
                            case TRESHOLD_OVER:
                                if( sample.getValue() + hysteresis <= threshold ){
                                    state = STATE_IDLE;
                                    onExit();
                                }
                                break;
                            case TRESHOLD_BELLOW:
                                if( sample.getValue() - hysteresis >= threshold ){
                                    state = STATE_IDLE;
                                    onExit();
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void save() {
        if( triggersManager != null ) {
            triggersManager.save();
        }
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
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

    public abstract void onChange();

    public abstract void onEnter();

    public abstract void onExit();

    public abstract void updateConfiguration();
}
