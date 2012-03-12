/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Engine;

import USBThermometerLib.*;
import com.almworks.sqlite4java.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paweł
 */
public class SQLite implements Database, Serializable {

    private String fileName;
    private SQLiteConnection queueDB;
    private SQLiteQueue queue;

    public SQLite() {
    }

    public SQLite(String fileName ) {
        this.fileName = fileName;
        initialize();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "sqlite:" + fileName;
    }

    @Override
    public void addSample(final Sample sample) {
        queue.execute(new SQLiteJob<Object>() {
            @Override
            protected Object job(SQLiteConnection arg0) throws SQLiteException {
                StringBuilder query = new StringBuilder();
                query.append("INSERT INTO ");
                query.append("S");
                query.append(sample.getSensor().getStringId());
                query.append(" VALUES(");
                query.append(sample.getDateTimeCreation().getTime());
                query.append(",");

                if( sample instanceof Temperature ) {
                    query.append(((Temperature)sample).getValue(Temperature.CELSIUS));
                } else {
                    query.append(sample.getValue());
                }

                query.append(");");

                int timeout = 100;
                while(timeout-- > 0) {
                    try {
                        queueDB.exec(query.toString());
                        break;
                    } catch(SQLiteBusyException ex) {
                        Delay.Ms(1);
                    }
                }
                return null;
            }
        }).complete();
    }

    @Override
    public void addSamples(final List<Sample> samples) {
        for(Sample sample : samples) {
            addSample(sample);
        }
    }

    @Override
    public Sample getLastSample(Sensor sensor) {
        try {
            SQLiteConnection ddb = new SQLiteConnection(new File(fileName));
            ddb.open();
            String query = "SELECT value, dateOfCreation FROM " +
                    "S" + sensor.getStringId() +
                    " ORDER BY dateOfCreation DESC LIMIT 1";

            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare(query);
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if (st != null) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        Date date = new Date(st.columnLong(1));
                        Sample sample = new Temperature(sensor, value, date);
                        return sample;
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return null;
    }

    @Override
    public List<Sample> getSamples(final Sensor sensor, final Date startDateTime, final Date stopDateTime, int limit, int order) {
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
        try {
            SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
            ddb.open();
            String query = "SELECT value, dateOfCreation FROM " +
                    "S" + sensor.getStringId() +
                    " WHERE dateOfCreation BETWEEN " +
                    startDateTime.getTime() +
                    " AND "+
                    stopDateTime.getTime() +
                    " ORDER BY dateOfCreation " +
                    (order == DESC ? "DESC" : "ASC") +
                    " LIMIT " +
                    limit;
            
            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare( query );
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if( st != null ) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        Date date = new Date(st.columnLong(1));
                        Sample sample = new Temperature(sensor, value, date);
                        samples.add(sample);
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return samples;
    }

    @Override
    public void addSensor(final Sensor sensor) {
        queue.execute(new SQLiteJob<Object>() {
            @Override
            protected Object job(SQLiteConnection arg0) throws SQLiteException {
                queueDB.exec("CREATE TABLE IF NOT EXISTS " +
                        "S" + sensor.getStringId() +
                        "( dateOfCreation DATETIME PRIMARY KEY ASC, value REAL )");

                String query = "INSERT INTO sensors VALUES ( NULL, '" +
                        sensor.getClass().getSimpleName() +
                        "','" +
                        sensor.getMedium() +
                        "','" +
                        sensor.getName() +
                        "'," +
                        sensor.getLongId() +
                        ")";

                int timeout = 100;
                while(timeout-- > 0) {
                    try {
                        queueDB.exec(query);
                        break;
                    } catch(SQLiteBusyException ex) {
                        Delay.Ms(1);
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void initialize() {
        queue = new SQLiteQueue(new File( fileName ));
        queue.start();
        queue.execute(new SQLiteJob<Object>() {
            @Override
            protected Object job(SQLiteConnection connection) throws SQLiteException {
                queueDB = new SQLiteConnection(new File( fileName ));
                try {
                    queueDB.open(true);
                    int timeout = 100;
                    while (timeout-- > 0) {
                        try {
                            queueDB.exec("CREATE TABLE IF NOT EXISTS sensors( i INTEGER PRIMARY KEY ASC, type VARCHAR(16), medium VARCHAR(16), name VARCHAR(256), id BIGINT UNIQUE )");
                            queueDB.exec("PRAGMA PAGE_SIZE = 4096");
                            queueDB.exec("PRAGMA default_cache_size=700000");
                            queueDB.exec("PRAGMA cache_size=700000");
                            queueDB.exec("PRAGMA compile_options");
                            break;
                        } catch (SQLiteBusyException ex) {
                            Delay.Ms(1);
                        }
                    }
                } catch (SQLiteException ex) {
                    Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                }
                return null;
            }
        }).complete();
    }

    @Override
    public void removeSensor(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateSensor(final Sensor sensor) {
        try {
            SQLiteConnection ddb = new SQLiteConnection(new File( fileName ));
            ddb.open();
            String query = "UPDATE sensors SET name='" + sensor.getName() + "' WHERE id=" + sensor.getLongId();

            int timeout = 100;
            while( timeout-- > 0 ) {
                try {
                    ddb.exec(query);
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }
            ddb.dispose();
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    @Override
    public List<Sensor> getSensors() {
        List<Sensor> sensors = new ArrayList<>();
        try {
            SQLiteConnection ddb = new SQLiteConnection(new File( fileName ));
            SQLiteStatement st;
            ddb.open();
            st = ddb.prepare("SELECT type, name, id FROM sensors");
            try {
                while (st.step()) {
                    String type = st.columnString(0);
                    String name = st.columnString(1);
                    long id = st.columnLong(2);
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
            } finally {
                st.dispose();
                ddb.dispose();
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return sensors;
    }

    @Override
    public List<Sample> getSamples(final Sensor sensor, final Date startDateTime, final Date stopDateTime, final long timespan, final int limit, final int orderBy ) {
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());

        if( timespan <= 0 ) {
            return samples;
        }

        SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
        try {
            ddb.open();

            int timeout = 100;
            while( timeout-- > 0 ) {
                try {
                    ddb.exec("begin");
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            long timerate = (stopDateTime.getTime() - startDateTime.getTime()) / timespan + 1;

            long i;
            for (i = 0; i < timerate; i++) {
                StringBuilder query = new StringBuilder();
                query.append("SELECT value, dateOfCreation FROM ");
                query.append("S");
                query.append(sensor.getStringId());
                query.append(" WHERE dateOfCreation BETWEEN ");
                query.append(orderBy == Database.ASC ? startDateTime.getTime() + timespan * i : stopDateTime.getTime() - timespan * i);
                query.append(" AND ");
                query.append(orderBy == Database.ASC ? startDateTime.getTime() + timespan * (i + 1) : stopDateTime.getTime() - timespan * (i + 1));
                query.append(" LIMIT 1; ");

                int samplesCount = 0;
                timeout = 100;
                while (timeout-- > 0) {
                    try {
                        SQLiteStatement st = ddb.prepare(query.toString());
                        try {
                            while (st.step()) {
                                double value = st.columnDouble(0);
                                Date date = new Date(st.columnLong(1));
                                Sample sample = new Temperature(sensor, value, date);
                                samples.add(sample);
                                samplesCount++;
                            }
                        } finally {
                            st.dispose();
                            break;
                        }
                    } catch (SQLiteBusyException ex) {
                        Delay.Ms(1);
                    }
                }

                if (samplesCount > limit) {
                    break;
                }
            }

            timeout = 100;
            while( timeout-- > 0 ) {
                try {
                    ddb.exec("end");
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        } finally {
            ddb.dispose();
        }
        return samples;
    }

    @Override
    public List<Sample> getSamples(final Sensor sensor, final Date startDateTime, final Date stopDateTime, final int limit, int order, double lowRange, double highRange ) {
        List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
        try {
            SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
            ddb.open();
            String query = "SELECT value, dateOfCreation FROM " +
                    "S" + sensor.getStringId() +
                    " WHERE dateOfCreation BETWEEN " +
                    startDateTime.getTime() +
                    " AND " +
                    stopDateTime.getTime() +
                    " AND value BETWEEN " +
                    lowRange +
                    " AND " +
                    highRange +
                    " ORDER BY dateOfCreation " +
                    (order == DESC ? "DESC" : "ASC") +
                    " LIMIT " +
                    limit;

            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare( query );
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if( st != null ) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        Date date = new Date(st.columnLong(1));
                        Sample sample = new Temperature(sensor, value, date);
                        samples.add(sample);
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return samples;
    }

    @Override
    public Sample getMaximumSample (Sensor sensor, Date startDateTime, Date stopDateTime) {
        Sample sample = null;
        try {
            SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
            ddb.open();
            String query = "SELECT MAX(value), dateOfCreation FROM " +
                    "S" + sensor.getStringId() +
                    " WHERE dateOfCreation BETWEEN " +
                    startDateTime.getTime() +
                    " AND "+
                    stopDateTime.getTime();

            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare( query );
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if( st != null ) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        Date date = new Date(st.columnLong(1));
                        sample = new Temperature(sensor, value, date);
                        break;
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return sample;
    }

    @Override
    public Sample getMinimumSample (Sensor sensor, Date startDateTime, Date stopDateTime) {
        Sample sample = null;
        try {
            SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
            ddb.open();
            String query = "SELECT MIN(value), dateOfCreation FROM " +
                    "S" + sensor.getStringId() +
                    " WHERE dateOfCreation BETWEEN " +
                    startDateTime.getTime() +
                    " AND "+
                    stopDateTime.getTime();

            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare( query );
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if( st != null ) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        Date date = new Date(st.columnLong(1));
                        sample = new Temperature(sensor, value, date);
                        break;
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return sample;
    }

    @Override
    public Sample getAverangeSample (Sensor sensor, Date startDateTime, Date stopDateTime) {
        Sample sample = null;
        try {
            SQLiteConnection ddb = new SQLiteConnection( new File( fileName ) );
            ddb.open();
            String query = "SELECT AVG(value) FROM " +
                    "S" + sensor.getStringId() +
                    " WHERE dateOfCreation BETWEEN " +
                    startDateTime.getTime() +
                    " AND "+
                    stopDateTime.getTime();

            SQLiteStatement st = null;
            int timeout = 100;
            while (timeout-- > 0) {
                try {
                    st = ddb.prepare( query );
                    break;
                } catch (SQLiteBusyException ex) {
                    Delay.Ms(1);
                }
            }

            if( st != null ) {
                try {
                    while (st.step()) {
                        double value = st.columnDouble(0);
                        sample = new Temperature(sensor, value, null);
                        break;
                    }
                } finally {
                    st.dispose();
                    ddb.dispose();
                }
            }
        } catch (SQLiteException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return sample;
    }
}
