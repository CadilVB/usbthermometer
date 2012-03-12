/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Engine.Host;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pawelkn
 */
public class TriggersManager {

    private static final String TRIGGERS_FILE_NAME = "trigger.xml";

    private Host host;
    private List<Trigger> triggers = new ArrayList<Trigger>();

    public TriggersManager(Host host) {
        this.host = host;

        load();
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public synchronized void addTrigger(Trigger trigger) {
        trigger.setTriggersManager(this);
        triggers.add(trigger);
        host.addHostObserver(trigger);
    }

    public synchronized void removeTrigger(Trigger trigger) {
        trigger.setTriggersManager(null);
        triggers.remove(trigger);
        host.removeHostObserver(trigger);
    }

    public synchronized void load() {
        for(Trigger trigger : triggers) {
            host.removeHostObserver(trigger);
        }

        triggers.clear();

        try {
            XMLDecoder decoder =
                new XMLDecoder(new BufferedInputStream(
                    new FileInputStream(TRIGGERS_FILE_NAME)));
            
            Trigger trigger;
            while( (trigger = (Trigger)decoder.readObject() ) != null ) {
                addTrigger(trigger);
            }
            decoder.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    public synchronized void save() {
        try {
            XMLEncoder encoder =
                new XMLEncoder(
                    new BufferedOutputStream(
                        new FileOutputStream(TRIGGERS_FILE_NAME)));
            for(Trigger trigger : triggers) {
                encoder.writeObject(trigger);
            }
            encoder.close();
        } catch (IOException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }
}
