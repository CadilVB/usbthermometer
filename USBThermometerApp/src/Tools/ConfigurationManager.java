/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tools;

import Main.Main;
import USBThermometerLib.Temperature;
import Visual.Graph.Graph;
import Visual.Graph.Grid;
import Visual.MainForm;
import java.awt.Color;
import java.awt.Component;
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
public class ConfigurationManager {

    private static final String CONFIGURATION_FILE_NAME = "config.xml";

    public static synchronized Configuration load() {
        Configuration configuration = null;

        try {
            XMLDecoder decoder =
                new XMLDecoder(new BufferedInputStream(
                    new FileInputStream(CONFIGURATION_FILE_NAME)));
            configuration = (Configuration)decoder.readObject();
            decoder.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }

        if( configuration == null ) {
            configuration = new Configuration();
            configuration.defaults();
            save( configuration, false );
        }

        return configuration;
    }

    public static synchronized void save(Configuration configuration) {
        save( configuration, true );
    }

    private static synchronized void save(Configuration configuration, boolean notiffyObservers) {
        try {
            XMLEncoder encoder =
                new XMLEncoder(
                    new BufferedOutputStream(
                        new FileOutputStream(CONFIGURATION_FILE_NAME)));
            encoder.writeObject(configuration);
            encoder.close();
        } catch (IOException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }

        if( notiffyObservers ) {
            notiffyConfigurationObservers(configuration);
        }
    }

    public static void notiffyConfigurationObservers(Configuration c) {

        if( c.getTemperatureUnit() == Configuration.CELCIUS ) {
            Temperature.setUnit(Temperature.CELSIUS);
        } else {
            Temperature.setUnit(Temperature.FAHRENHEIT);
        }

        Graph.setColorList(getColorList(c));
        Grid.setBackground(new Color(c.getGraphBackgroundColor()));
        Grid.setForeground(new Color(c.getGraphForegroundColor()));

        for(Component cmp : MainForm.getInstance().getVisualElemets() ) {
            cmp.repaint();
        }

        if( c.isStartupOnWindowsStartUp() ) {
            WinStartUp.set(Main.getExePath());
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

        if( c.isShowInTray() ) {
            if( !MainForm.getInstance().isInTray() ) {
                MainForm.getInstance().addToSystemTray();
            }
        } else {
            MainForm.getInstance().removeFromSystemTray();
        }

        Main.getLocalhost().setSamplingRate(c.getSamplingRate());
    }

    private static List<Color> getColorList(Configuration configuration) {
        List<Color> colorList = new ArrayList<Color>();
        colorList.add(new Color(configuration.getSet1PreferedColor()));
        colorList.add(new Color(configuration.getSet2PreferedColor()));
        colorList.add(new Color(configuration.getSet3PreferedColor()));
        colorList.add(new Color(configuration.getSet4PreferedColor()));
        colorList.add(new Color(configuration.getSet5PreferedColor()));
        colorList.add(new Color(configuration.getSet6PreferedColor()));
        colorList.add(new Color(configuration.getSet7PreferedColor()));
        colorList.add(new Color(configuration.getSet8PreferedColor()));
        colorList.add(new Color(configuration.getSet9PreferedColor()));
        colorList.add(new Color(configuration.getSet10PreferedColor()));
        return colorList;
    }
}
