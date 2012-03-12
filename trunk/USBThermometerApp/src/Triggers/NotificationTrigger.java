/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Visual.Triggers.NotificationTriggerConfigurationPanel;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

/**
 *
 * @author Paweł
 */
public class NotificationTrigger extends Trigger {

    private NotificationTriggerConfigurationPanel c;

    private boolean enableTrayNotification = false;
    private boolean enableSoundNotification = false;
    private String trayNotification = "";
    private String trayCaption = "USB Thermometer";
    private String soundFile = "";

    public NotificationTrigger() {
        super();

        setName("Notification Trigger");
    }

    public boolean isEnableSoundNotification() {
        return enableSoundNotification;
    }

    public void setEnableSoundNotification(boolean enableSoundNotification) {
        this.enableSoundNotification = enableSoundNotification;
    }

    public boolean isEnableTrayNotification() {
        return enableTrayNotification;
    }

    public void setEnableTrayNotification(boolean enableTrayNotification) {
        this.enableTrayNotification = enableTrayNotification;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getTrayNotification() {
        return trayNotification;
    }

    public void setTrayNotification(String trayNotification) {
        this.trayNotification = trayNotification;
    }

    public String getTrayCaption() {
        return trayCaption;
    }

    public void setTrayCaption(String trayCaption) {
        this.trayCaption = trayCaption;
    }

    @Override
    public JPanel getConfigurationPanel() {
        c = new NotificationTriggerConfigurationPanel();
        c.setEnableSoundNotification(enableSoundNotification);
        c.setEnableTrayNotification(enableTrayNotification);
        c.setTrayNotification(trayNotification);
        c.setSoundFile(soundFile);
        c.setTrayCaption(trayCaption);
        return c;
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onEnter() {
        if( enableTrayNotification ) {
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                TrayIcon[] trayIcons = tray.getTrayIcons();
                if( trayIcons.length > 0 ) {
                    trayIcons[0].displayMessage(trayCaption, trayNotification, TrayIcon.MessageType.NONE);
                }
            }
        }

        if( enableSoundNotification ) {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(soundFile));
                clip.open(inputStream);
                clip.start();
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public void updateConfiguration() {
        if( c != null ) {
            enableSoundNotification = c.isEnableSoundNotification();
            enableTrayNotification = c.isEnableTrayNotification();
            trayNotification = c.getTrayNotification();
            soundFile = c.getSoundFile();
            trayCaption = c.getTrayCaption();
        }
    }

}
