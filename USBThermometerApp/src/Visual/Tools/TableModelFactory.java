/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import Engine.Database;
import USBThermometerLib.Sample;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import USBThermometerLib.Sensor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author pawelkn
 */
public class TableModelFactory {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static ResourceBundle bundle;

    public static TableModel getTableModel(List<Sample[]> samples, List<Sensor> sensors, final int orderBy, int limit) {
        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        Hashtable columnsHashTable = new Hashtable();
        for(Sensor sensor: sensors) {
            columnsHashTable.put(sensor.getName(), columnsHashTable.size() + 2);
        }

        String[] columns = new String[2 + columnsHashTable.size()];
        columns[0] = bundle.getString("DATE");
        columns[1] = bundle.getString("TIME");
        for (Enumeration e = columnsHashTable.keys(); e.hasMoreElements();) {
            String sensorName = (String) (e.nextElement());
            columns[(Integer) (columnsHashTable.get(sensorName))] = sensorName;
        }

        if ( ( samples != null ) && ( samples.size() > 0 ) ) {
            List<String[]> val = new ArrayList<String[]>();
            List<Sample> samplesBag = new ArrayList<Sample>();

            for( int i = 0; i < samples.size(); i++ ) {
                for( int j = 0; j < samples.get(i).length ; j++ ) {
                    samplesBag.add(samples.get(i)[j]);
                }
            }

            Collections.sort(samplesBag,new Comparator<Sample>(){
                @Override
                public int compare(Sample o1, Sample o2) {
                    if( orderBy == Database.ASC ) {
                        return (int)(o1.getDateTimeCreation().getTime() - o2.getDateTimeCreation().getTime());
                    } else {
                        return (int)(o2.getDateTimeCreation().getTime() - o1.getDateTimeCreation().getTime());
                    }
                }
            });

            long lastDate = ( orderBy == Database.ASC ? Long.MIN_VALUE : Long.MAX_VALUE );
            String[] row = null;

            for(Sample sample : samplesBag) {
                if( ( ( orderBy == Database.ASC ) && ( sample.getDateTimeCreation().getTime() > lastDate ) ) ||
                    ( ( orderBy == Database.DESC ) && ( sample.getDateTimeCreation().getTime() < lastDate ) ) ) {
                    if( row != null ) {
                        val.add(row);
                        if( val.size() >= limit ) {
                            break;
                        }
                    }
                    row = new String[2 + samples.size()];
                    row[0] = dateFormat.format( sample.getDateTimeCreation() );
                    row[1] = timeFormat.format( sample.getDateTimeCreation() );
                    lastDate = sample.getDateTimeCreation().getTime();
                }

                String sensorName = sample.getSensor().getName();
                Integer columnNumber = (Integer)(columnsHashTable.get(sensorName));
                if( columnNumber != null ) {
                    row[columnNumber] = String.valueOf(trunc( sample.getValue()) ).replace('.', ',');
                }
            }

            return new DefaultTableModel( val.toArray(new String[0][0]), columns ) {
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            };
        }

        return new DefaultTableModel();
    }

    private static double trunc(double x) {
        double invBase = 10;
        if ( x > 0 )
            return Math.floor(x * invBase)/invBase;
        else
            return Math.ceil(x * invBase)/invBase;
    }

}
