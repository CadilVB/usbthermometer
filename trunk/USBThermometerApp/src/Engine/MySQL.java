/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import USBThermometerLib.*;
import java.io.Serializable;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pawelkn
 */
public class MySQL implements Database, Serializable {

    private MySQL.MyConnection myConnection;
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String hostName;
    private int hostPort;
    private String user;
    private String password;
    private String database;

    public MySQL() {
    }

    public MySQL(String hostName, int hostPort, String user, String password, String database) {
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.user = user;
        this.password = password;
        this.database = database;
        initialize();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void initialize() {
        myConnection = new MySQL.MyConnection();

        Connection con = myConnection.getConnection();
        if (con != null) {
            try (Statement stmt = con.createStatement();) {
                String update = "CREATE TABLE IF NOT EXISTS sensors( i INTEGER NOT NULL auto_increment, type VARCHAR(16), medium VARCHAR(16), name VARCHAR(256), id BIGINT UNIQUE, PRIMARY KEY(i) )";
                stmt.executeUpdate(update);
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    private class MyConnection {

        private Connection con;

        public MyConnection() {
        }

        public synchronized Connection getConnection() {
            try {
                if ((con == null) || !con.isValid(1)) {
                    Class.forName("com.mysql.jdbc.Driver");
                    String connectionUrl = "jdbc:mysql://" + hostName + ":" + hostPort + "/" + database + "?" + "user=" + user + "&password=" + password;
                    con = DriverManager.getConnection(connectionUrl);
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
            return con;
        }
    }

    @Override
    public String toString() {
        return "mysql:" + hostName + "/" + database;
    }

    @Override
    public void addSample(Sample sample) {
        Connection con = myConnection.getConnection();
        if (con != null) {
            StringBuilder update = new StringBuilder();
            update.append("INSERT INTO ");
            update.append("S");
            update.append(sample.getSensor().getStringId());
            update.append(" VALUES( '");
            update.append(df.format(sample.getDateTimeCreation()));
            update.append("',");
            if (sample instanceof Temperature) {
                update.append(((Temperature) sample).getValue(Temperature.CELSIUS));
            } else {
                update.append(sample.getValue());
            }
            update.append(");");
            try (Statement stmt = con.createStatement();) {
                stmt.executeUpdate(update.toString());
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    @Override
    public void addSamples(List<Sample> samples) {
        for (Sample sample : samples) {
            addSample(sample);
        }
    }

    @Override
    public Sample getLastSample(Sensor sensor) {
        Sample sample = null;
        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT value, dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " ORDER BY dateOfCreation DESC LIMIT 1";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    sample = new Temperature(sensor, value, date);
                    break;
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return sample;
    }

    @Override
    public List<Sample> getSamples(Sensor sensor, Date startDateTime, Date stopDateTime, int limit, int orderBy) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT value, dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " WHERE dateOfCreation BETWEEN '"
                    + df.format(startDateTime)
                    + "' AND '"
                    + df.format(stopDateTime)
                    + "' ORDER BY dateOfCreation "
                    + (orderBy == DESC ? "DESC" : "ASC")
                    + " LIMIT "
                    + limit;
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    Sample sample = new Temperature(sensor, value, date);
                    samples.add(sample);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return samples;
    }

    @Override
    public List<Sample> getSamples(Sensor sensor, Date startDateTime, Date stopDateTime, int limit, int orderBy, double lowRange, double highRange) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT value, dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " WHERE dateOfCreation BETWEEN '"
                    + df.format(startDateTime)
                    + "' AND '"
                    + df.format(stopDateTime)
                    + "' AND value BETWEEN "
                    + lowRange
                    + " AND "
                    + highRange
                    + " ORDER BY dateOfCreation "
                    + (orderBy == DESC ? "DESC" : "ASC")
                    + " LIMIT "
                    + limit;
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    Sample sample = new Temperature(sensor, value, date);
                    samples.add(sample);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return samples;
    }

    @Override
    public List<Sample> getSamples(Sensor sensor, Date startDateTime, Date stopDateTime, long timespan, int limit, int orderBy) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
        
        if(timespan <= 0) {
            return samples;
        }
        
        long timerate = (stopDateTime.getTime() - startDateTime.getTime()) / timespan + 1;

        StringBuilder query = new StringBuilder();
        for (long i = 0; i < timerate; i++) {
            query.append("(SELECT value, dateOfCreation FROM ");
            query.append("S");
            query.append(sensor.getStringId());
            query.append(" WHERE dateOfCreation BETWEEN '");
            query.append(df.format(new Date(orderBy == Database.ASC ? startDateTime.getTime() + timespan * i : stopDateTime.getTime() - timespan * i)));
            query.append("' AND '");
            query.append(df.format(new Date(orderBy == Database.ASC ? startDateTime.getTime() + timespan * (i + 1) : stopDateTime.getTime() - timespan * (i + 1))));
            query.append("' LIMIT 1)");
            if (i < timerate - 1) {
                query.append(" UNION ");
            }
        }

        Connection con = myConnection.getConnection();
        if (con != null) {
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query.toString());) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    Sample sample = new Temperature(sensor, value, date);
                    samples.add(sample);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return samples;
    }

    @Override
    public Sample getMaximumSample(Sensor sensor, Date startDateTime, Date stopDateTime) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        Sample sample = null;

        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT MAX(value), dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " WHERE dateOfCreation BETWEEN '"
                    + df.format(startDateTime)
                    + "' AND '"
                    + df.format(stopDateTime)
                    + "'";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getDate(2);
                    sample = new Temperature(sensor, value, date);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return sample;
    }

    @Override
    public Sample getMinimumSample(Sensor sensor, Date startDateTime, Date stopDateTime) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        Sample sample = null;

        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT MIN(value), dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " WHERE dateOfCreation BETWEEN '"
                    + df.format(startDateTime)
                    + "' AND '"
                    + df.format(stopDateTime)
                    + "'";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    sample = new Temperature(sensor, value, date);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return sample;
    }

    @Override
    public Sample getAverangeSample(Sensor sensor, Date startDateTime, Date stopDateTime) {
        stopDateTime = correctMaxStopDate(stopDateTime);
        Sample sample = null;

        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT AVG(value), dateOfCreation FROM "
                    + "S" + sensor.getStringId()
                    + " WHERE dateOfCreation BETWEEN '"
                    + df.format(startDateTime)
                    + "' AND '"
                    + df.format(stopDateTime)
                    + "'";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    double value = rs.getDouble(1);
                    Date date = rs.getTimestamp(2);
                    sample = new Temperature(sensor, value, date);
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return sample;
    }

    @Override
    public void addSensor(Sensor sensor) {
        Connection con = myConnection.getConnection();
        if (con != null) {
            String update = "CREATE TABLE IF NOT EXISTS " + "S" + sensor.getStringId() + "( dateOfCreation DATETIME NOT NULL, value REAL, PRIMARY KEY(dateOfCreation) )";
            try (Statement stmt = con.createStatement();) {
                stmt.executeUpdate(update);
                update = "INSERT INTO sensors VALUES ( NULL, '" + sensor.getClass().getSimpleName() + "','" + sensor.getMedium() + "','" + sensor.getName() + "'," + sensor.getLongId() + ")";
                stmt.executeUpdate(update);
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    @Override
    public void removeSensor(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateSensor(Sensor sensor) {
        Connection con = myConnection.getConnection();
        if (con != null) {
            String update = "UPDATE sensors SET name='" + sensor.getName() + "' WHERE id=" + sensor.getLongId();
            try (Statement stmt = con.createStatement();) {
                stmt.executeUpdate(update);
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    @Override
    public List<Sensor> getSensors() {
        List<Sensor> sensors = new ArrayList<>();

        Connection con = myConnection.getConnection();
        if (con != null) {
            String query = "SELECT type, name, id FROM sensors";
            try (Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);) {
                while (rs.next()) {
                    String type = rs.getString(1);
                    String name = rs.getString(2);
                    long id = rs.getLong(3);
                    switch (type) {
                        case "DS18B20":
                            {
                                Sensor sensor = new DS18B20(Sensor.convertLongToByte(id), null);
                                sensor.setName(name);
                                sensors.add(sensor);
                                break;
                            }
                        case "DS18S20":
                            {
                                Sensor sensor = new DS18S20(Sensor.convertLongToByte(id), null);
                                sensor.setName(name);
                                sensors.add(sensor);
                                break;
                            }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        return sensors;
    }

    private Date correctMaxStopDate(Date stopDateTime) {
        try {
            Date maxDate = df.parse("9999-12-31 23:59:59.000");
            if (stopDateTime.getTime() > maxDate.getTime()) {
                stopDateTime = maxDate;
            }
        } catch (ParseException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return stopDateTime;
    }
}
