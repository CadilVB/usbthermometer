/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TablePanel.java
 *
 * Created on 2011-10-27, 11:15:22
 */

package Visual;

import Engine.Host;
import Engine.HostObserver;
import USBThermometerLib.Device;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import Main.Main;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import Visual.Tools.BetterTable;
import Visual.Tools.ExcelExporter;
import Visual.Tools.ExtensionFileFilter;
import Visual.Tools.InternalFrameObservable;
import Visual.Tools.TableModelFactory;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author pawelkn
 */
public class TablePanel extends javax.swing.JPanel implements DropTargetListener, HostObserver {

    private final Component parent;
    private final List<Sensor> sensors = new ArrayList<Sensor>();
    private final List<Sample[]> samples = new ArrayList<Sample[]>();

    private static final TableModel NULL_TABLE_MODEL = new DefaultTableModel( new Object [][] {},new String [] {} );
    private static ResourceBundle bundle;
    private final TableSettingsDialog tsd = new TableSettingsDialog(MainForm.getInstance(),true);
    private final BetterTable jTable1 = new BetterTable(NULL_TABLE_MODEL);

    /** Creates new form TablePanel */
    public TablePanel(Component parent) {
        this.parent = parent;

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        initComponents();

        jScrollPane1.setViewportView(jTable1);

        jScrollPane1.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if( !jTable1.isMouseIn() ) {
                    jTable1.clearSelection();
                }
            }
        });

        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });

        this.setDropTarget(new DropTarget(this,this));
        Main.getLocalhost().addHostObserver(this);
    }

    public void close() {
        Main.getLocalhost().removeHostObserver(this);
        MainForm.getInstance().removeVisualElement(parent);
        tsd.dispose();
    }

    public TableSettingsDialog getTableSettingsDialog() {
        return tsd;
    }

    public synchronized List<Sensor> getSensors() {
        return sensors;
    }

    public void updateTable() {
        samples.clear();

        if (tsd.getDateTimeType() == TableSettingsDialog.LAST_SAVED) {
            for (Sensor sensor : sensors) {
                samples.add((Main.getLocalhost().getDatabase().getSamples(sensor, new Date(0), new Date(Long.MAX_VALUE),
                        tsd.getLastSavedValue(), TableSettingsDialog.DESC)).toArray(new Sample[0]));
            }
            jTable1.setModel(TableModelFactory.getTableModel(samples, sensors, TableSettingsDialog.DESC, tsd.getLastSavedValue()));
        } else {
            for (Sensor sensor : sensors) {
                Date startDate;
                Date stopDate;
                if (tsd.getDateTimeType() == TableSettingsDialog.PERIOD) {
                    startDate = tsd.getPeriodLowDate();
                    stopDate = tsd.getPeriodHighDate();
                } else {
                    Calendar cStop = Calendar.getInstance();
                    cStop.add(Calendar.SECOND, - 10);
                    stopDate = cStop.getTime();

                    Calendar cStart = Calendar.getInstance();
                    cStart.add(tsd.getLastTimespanType(), -tsd.getLastTimespan());
                    cStart.add(Calendar.SECOND, - 10);
                    startDate = cStart.getTime();
                }

                samples.add((Main.getLocalhost().getDatabase().getSamples(sensor, startDate, stopDate,
                        tsd.getLimit(), tsd.getOrderBy(), tsd.getLowRange(), tsd.getHighRange())).toArray(new Sample[0]));
            }
            jTable1.setModel(TableModelFactory.getTableModel(samples, sensors, tsd.getOrderBy(), tsd.getLimit()));
        }

        updateFrameTitle();
    }

    protected void updateFrameTitle() {
        if( ( parent != null ) && ( parent instanceof InternalFrameObservable ) ) {
            InternalFrameObservable ifo = (InternalFrameObservable) (parent);
            if (sensors.size() == 0 ) {
                ifo.setTitle(bundle.getString("TABLE"));
                ifo.notifyTitleChanged(bundle.getString("TABLE"));
            } else if(sensors.size() == 1 ) {
                ifo.setTitle(bundle.getString("TABLE_-_") + sensors.get(0).getName());
                ifo.notifyTitleChanged(bundle.getString("TABLE_-_") + sensors.get(0).getName());
            } else {
                ifo.setTitle(bundle.getString("TABLE_-_") + sensors.get(0).getName() + ", ...");
                ifo.notifyTitleChanged(bundle.getString("TABLE_-_") + sensors.get(0).getName() + ", ...");
            }
        }
    }

    public void addSensor(Sensor sensor) {
        if (sensor != null) {
            for (Sensor s : sensors) {
                if (s == sensor) {
                    return;
                }
            }
            sensors.add(sensor);

            updateTable();
            updateFrameTitle();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jScrollPane1 = new javax.swing.JScrollPane();

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton1.setIcon(new ImageIcon("graphics/clock--pencil.png"));
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);
        jToolBar1.add(jSeparator1);

        jButton2.setIcon(new ImageIcon("graphics/document-excel-csv.png"));
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);
        jToolBar1.add(jSeparator2);

        jButton3.setIcon(new ImageIcon("graphics/arrow-circle-double-135.png"));
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setIcon(new ImageIcon("graphics/cross-script.png"));
        jButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jToolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:        
        tsd.setVisible(true);
        if( tsd.getReturnStatus() == TableSettingsDialog.RET_OK ) {
            updateTable();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        int count = sensors.size();
        Object[] possibilities = new Object[count];
        for( int i = 0; i < count; i++ ) {
            possibilities[i] = sensors.get(i).getName();
        }

        String s = (String)JOptionPane.showInputDialog(
                            parent,
                            bundle.getString("SELECT_A_SENSOR_TO_REMOVE:"),
                            bundle.getString("REMOVE_SENSOR"),
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            possibilities,
                            null);

        if( s != null ) {
            for( Sensor sensor : sensors ) {
                if(s.equals(sensor.getName())) {
                    sensors.remove(sensor);

                    updateTable();
                    updateFrameTitle();
                    return;
                }
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        updateTable();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String wd = System.getProperty("user.dir");
        JFileChooser fc = new JFileChooser(wd);
        FileFilter filter1 = new ExtensionFileFilter(bundle.getString("EXCEL_FILE_(*.XLS)"), new String[] { "XLS" });
        fc.setFileFilter(filter1);
        int rc = fc.showDialog(null, bundle.getString("SAVE"));
        if (rc == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                String filePath = f.getPath();
                if(!filePath.toLowerCase().endsWith(".xls"))
                {
                    f = new File(filePath + ".xls");
                }
                ExcelExporter.exportTable(jTable1, f, bundle.getString("CHARSET"));
            } catch (IOException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if( evt.isControlDown() && (evt.getKeyCode() == KeyEvent.VK_C) ) {
            TransferHandler th = jTable1.getTransferHandler();
            if (th != null) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                th.exportToClipboard(jTable1, cb, TransferHandler.COPY);
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void dragEnter(DropTargetDragEvent arg0) {
    }

    @Override
    public void dragOver(DropTargetDragEvent arg0) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent arg0) {
    }

    @Override
    public void dragExit(DropTargetEvent arg0) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        List<DataFlavor> df = dtde.getCurrentDataFlavorsAsList();
        Transferable tr = dtde.getTransferable();
        for(DataFlavor d : df) {
            try {
                Object list = tr.getTransferData(d);
                if( list instanceof String ) {
                    Sensor newSensor = Main.findSensorByName((String)list);
                    if( newSensor != null ) {
                        addSensor(newSensor);
                        return;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
        jTable1.setModel(TableModelFactory.getTableModel(samples, sensors, TableSettingsDialog.DESC, tsd.getLastSavedValue()));
        updateFrameTitle();
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
