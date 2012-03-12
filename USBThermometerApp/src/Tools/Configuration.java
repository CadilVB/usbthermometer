/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tools;

import Engine.Database;
import Engine.SQLite;
import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Paweł
 */
    public class Configuration implements Serializable {
        public static final int CELCIUS = 0;
        public static final int FARANHIT = 1;

        private Database database;

        private boolean guiMode;

        private int temperatureUnit;
        private int samplingRate;

        private boolean startupOnWindowsStartUp;
        private boolean startMinimized;
        private boolean lockMultipleStartsUp;
        private boolean showInTray;
        private boolean minimizeToTray;
        private boolean closeToTray;

        private int graphBackgroundColor;
        private int graphForegroundColor;
        private int set1PreferedColor;
        private int set2PreferedColor;
        private int set3PreferedColor;
        private int set4PreferedColor;
        private int set5PreferedColor;
        private int set6PreferedColor;
        private int set7PreferedColor;
        private int set8PreferedColor;
        private int set9PreferedColor;
        private int set10PreferedColor;
       
        private String locale;

        private boolean useProxy;
        private String proxyHost;
        private String proxyPort;
        private boolean proxyAutorization;
        private String proxyUser;
        private String proxyPassword;

        public void defaults() {
            temperatureUnit = CELCIUS;
            samplingRate = 10;

            database = new SQLite("USBThermometer.db");

            guiMode = true;

            startupOnWindowsStartUp = true;
            startMinimized = false;
            lockMultipleStartsUp = true;
            showInTray = true;
            minimizeToTray = true;
            closeToTray = true;

            graphBackgroundColor = Color.WHITE.getRGB();
            graphForegroundColor = Color.GRAY.getRGB();
            set1PreferedColor = 0x0099cc;
            set2PreferedColor = Color.GREEN.getRGB();
            set3PreferedColor = Color.RED.getRGB();
            set4PreferedColor = Color.MAGENTA.getRGB();
            set5PreferedColor = Color.ORANGE.getRGB();
            set6PreferedColor = Color.PINK.getRGB();
            set7PreferedColor = Color.YELLOW.getRGB();
            set8PreferedColor = Color.BLUE.getRGB();
            set9PreferedColor = Color.LIGHT_GRAY.getRGB();
            set10PreferedColor = Color.CYAN.getRGB();

            locale = "en_US";

            useProxy = false;
            proxyHost = "";
            proxyPort = "";
            proxyAutorization = false;
            proxyUser = "";
            proxyPassword = "";
        }

        public boolean isStartMinimized() {
            return startMinimized;
        }

        public void setStartMinimized(boolean startMinimized) {
            this.startMinimized = startMinimized;
        }        
        
        public Database getDatabase() {
            return database;
        }

        public boolean isGuiMode() {
            return guiMode;
        }

        public void setGuiMode(boolean guiMode) {
            this.guiMode = guiMode;
        }

        public void setDatabase(Database database) {
            this.database = database;
        }

        public int getSet10PreferedColor() {
            return set10PreferedColor;
        }

        public void setSet10PreferedColor(int set10PreferedColor) {
            this.set10PreferedColor = set10PreferedColor;
        }

        public int getSet1PreferedColor() {
            return set1PreferedColor;
        }

        public void setSet1PreferedColor(int set1PreferedColor) {
            this.set1PreferedColor = set1PreferedColor;
        }

        public int getSet2PreferedColor() {
            return set2PreferedColor;
        }

        public void setSet2PreferedColor(int set2PreferedColor) {
            this.set2PreferedColor = set2PreferedColor;
        }

        public int getSet3PreferedColor() {
            return set3PreferedColor;
        }

        public void setSet3PreferedColor(int set3PreferedColor) {
            this.set3PreferedColor = set3PreferedColor;
        }

        public int getSet4PreferedColor() {
            return set4PreferedColor;
        }

        public void setSet4PreferedColor(int set4PreferedColor) {
            this.set4PreferedColor = set4PreferedColor;
        }

        public int getSet5PreferedColor() {
            return set5PreferedColor;
        }

        public void setSet5PreferedColor(int set5PreferedColor) {
            this.set5PreferedColor = set5PreferedColor;
        }

        public int getSet6PreferedColor() {
            return set6PreferedColor;
        }

        public void setSet6PreferedColor(int set6PreferedColor) {
            this.set6PreferedColor = set6PreferedColor;
        }

        public int getSet7PreferedColor() {
            return set7PreferedColor;
        }

        public void setSet7PreferedColor(int set7PreferedColor) {
            this.set7PreferedColor = set7PreferedColor;
        }

        public int getSet8PreferedColor() {
            return set8PreferedColor;
        }

        public void setSet8PreferedColor(int set8PreferedColor) {
            this.set8PreferedColor = set8PreferedColor;
        }

        public int getSet9PreferedColor() {
            return set9PreferedColor;
        }

        public void setSet9PreferedColor(int set9PreferedColor) {
            this.set9PreferedColor = set9PreferedColor;
        }

        public boolean isCloseToTray() {
            return closeToTray;
        }

        public void setCloseToTray(boolean closeToTray) {
            this.closeToTray = closeToTray;
        }

        public int getGraphBackgroundColor() {
            return graphBackgroundColor;
        }

        public void setGraphBackgroundColor(int graphBackgroundColor) {
            this.graphBackgroundColor = graphBackgroundColor;
        }

        public int getGraphForegroundColor() {
            return graphForegroundColor;
        }

        public void setGraphForegroundColor(int graphForegroundColor) {
            this.graphForegroundColor = graphForegroundColor;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public boolean isLockMultipleStartsUp() {
            return lockMultipleStartsUp;
        }

        public void setLockMultipleStartsUp(boolean lockMultipleStartsUp) {
            this.lockMultipleStartsUp = lockMultipleStartsUp;
        }

        public boolean isMinimizeToTray() {
            return minimizeToTray;
        }

        public void setMinimizeToTray(boolean minimizeToTray) {
            this.minimizeToTray = minimizeToTray;
        }

        public boolean isProxyAutorization() {
            return proxyAutorization;
        }

        public void setProxyAutorization(boolean proxyAutorization) {
            this.proxyAutorization = proxyAutorization;
        }

        public String getProxyHost() {
            return proxyHost;
        }

        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }

        public String getProxyPassword() {
            return proxyPassword;
        }

        public void setProxyPassword(String proxyPassword) {
            this.proxyPassword = proxyPassword;
        }

        public String getProxyPort() {
            return proxyPort;
        }

        public void setProxyPort(String proxyPort) {
            this.proxyPort = proxyPort;
        }

        public String getProxyUser() {
            return proxyUser;
        }

        public void setProxyUser(String proxyUser) {
            this.proxyUser = proxyUser;
        }

        public int getSamplingRate() {
            return samplingRate;
        }

        public void setSamplingRate(int samplingRate) {
            this.samplingRate = samplingRate;
        }

        public boolean isShowInTray() {
            return showInTray;
        }

        public void setShowInTray(boolean showInTray) {
            this.showInTray = showInTray;
        }

        public boolean isStartupOnWindowsStartUp() {
            return startupOnWindowsStartUp;
        }

        public void setStartupOnWindowsStartUp(boolean startupOnWindowsStartUp) {
            this.startupOnWindowsStartUp = startupOnWindowsStartUp;
        }

        public int getTemperatureUnit() {
            return temperatureUnit;
        }

        public void setTemperatureUnit(int temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
        }

        public boolean isUseProxy() {
            return useProxy;
        }

        public void setUseProxy(boolean useProxy) {
            this.useProxy = useProxy;
        }
    }
