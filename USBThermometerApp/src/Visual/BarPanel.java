/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DummyPanel.java
 *
 * Created on Oct 2, 2011, 12:04:45 PM
 */

package Visual;

import Visual.Tools.InternalFrameObservable;
import Main.Main;
import USBThermometerLib.Device;
import Engine.Host;
import Engine.HostObserver;
import Engine.NullDevice;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.Temperature;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import Visual.Tools.InternalFrameAllwaysOnTop;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author Paweł
 */
public class BarPanel extends javax.swing.JPanel implements HostObserver, DropTargetListener {

    public static final int MAX_WIDTH = Integer.MAX_VALUE;
    private static ResourceBundle bundle;

    private Sensor sensor;
    private Component parent;

    private boolean updateFrame = false;

    private final Icon arrowUpIcon = new ImageIcon("graphics/arrow-curve-090-left.png");
    private final Icon arrowDownIcon = new ImageIcon("graphics/arrow-stop-270.png");

    private JButton formBtn = null;

    /** Creates new form DummyPanel */
    public BarPanel(Component parent) {
        this.parent = parent;

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        initComponents();

        if( parent instanceof JInternalFrame ) {
            ((JInternalFrame)parent).setMinimumSize( getMinimumSize() );
        } else if( parent instanceof JDialog ) {
            ((JDialog)parent).setMinimumSize( getMinimumSize() );
        }

        progressBar.setValue(progressBar.getMinimum());
        progressBar.setVisible(false);
        jLabel1.setVisible(false);
        
        this.setDropTarget(new DropTarget(this,this));
    }

    private void createFormButton() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                formBtn = new JButton();
                formBtn.setBounds(0, 0, 18, 18);
                formBtn.setAlignmentY(0.0F);
                formBtn.setPreferredSize(new java.awt.Dimension(18, 18));
                formBtn.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        buttonAction(evt);
                    }
                });

                JPanel glass = null;

                if (parent instanceof JInternalFrame) {
                    glass = (JPanel) (((JInternalFrame) parent).getGlassPane());
                    formBtn.setIcon(arrowUpIcon);
                } else if (parent instanceof JDialog) {
                    glass = (JPanel) (((JDialog) parent).getGlassPane());
                    formBtn.setIcon(arrowDownIcon);
                }

                if (glass != null) {
                    glass.setOpaque(false);
                    glass.setVisible(true);
                    glass.setLayout(null);
                    glass.add(formBtn);
                }
            }
        });
    }

    public void close() {
        if( !updateFrame ) {
            Main.getLocalhost().removeHostObserver(this);
            MainForm.getInstance().removeVisualElement(parent);
        }
        updateFrame = false;
    }

    public Sensor getSensor() {
        return sensor;
    }

    private void setIconArrowDown() {
        if( formBtn != null ) {
            formBtn.setIcon(arrowDownIcon);
        }
    }

    private void setIconArrowUp() {
        if( formBtn != null ) {
            formBtn.setIcon(arrowUpIcon);
        }
    }

    public void setSensor(Sensor newSensor) {
        if( newSensor != null ) {
            if (this.sensor != newSensor) {
                this.sensor = newSensor;
                progressBar.setVisible(true);
                jLabel1.setVisible(true);
                jLabel2.setVisible(true);
                jLabel2.setText(sensor.getName());
                Main.getLocalhost().addHostObserver(this);
                if( !(sensor.getDevice() instanceof NullDevice ) ) {
                    jLabel2.setText(sensor.getName());
                    Sample sample = sensor.getLastSample();
                    jLabel1.setText(Double.toString(sample.getValue()) + Temperature.getUnitString());
                    progressBar.setValue((int)(sample.getValue()));
                } else {
                    notiffyDeviceRemoved(null, null);
                }
                updateFrameTitle();
                createFormButton();
                repaint();
            }
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

        jPanel1 = new javax.swing.JPanel();
        progressBar = new Visual.Tools.TemperatureBarChart();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(140, 32767));
        setMinimumSize(new java.awt.Dimension(140, 73));
        setPreferredSize(new java.awt.Dimension(140, 100));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 2));
        jPanel1.setPreferredSize(new java.awt.Dimension(125, 2));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        add(jPanel1);

        progressBar.setMaximumSize(new java.awt.Dimension(32, 32767));

        javax.swing.GroupLayout progressBarLayout = new javax.swing.GroupLayout(progressBar);
        progressBar.setLayout(progressBarLayout);
        progressBarLayout.setHorizontalGroup(
            progressBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );
        progressBarLayout.setVerticalGroup(
            progressBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        add(progressBar);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanel2.setMinimumSize(new java.awt.Dimension(0, 2));
        jPanel2.setPreferredSize(new java.awt.Dimension(125, 2));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        add(jPanel2);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(bundle.getString("DRAG&DROP_A_SENSOR")); // NOI18N
        jLabel2.setAlignmentX(0.5F);
        add(jLabel2);

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 2));
        jPanel3.setPreferredSize(new java.awt.Dimension(125, 2));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        add(jPanel3);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("NOT_CONNECTED")); // NOI18N
        jLabel1.setAlignmentX(0.5F);
        add(jLabel1);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanel4.setMinimumSize(new java.awt.Dimension(0, 2));
        jPanel4.setPreferredSize(new java.awt.Dimension(125, 2));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
        );

        add(jPanel4);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private Visual.Tools.TemperatureBarChart progressBar;
    // End of variables declaration//GEN-END:variables

    private void buttonAction(ActionEvent evt) {
        updateFrame = true;

        if (parent instanceof JInternalFrame) {
            final JDialog dialog = new JDialog();
            dialog.setTitle(bundle.getString("BAR_GRAPH"));
            dialog.setSize(parent.getSize());
            dialog.setMinimumSize(getMinimumSize());
            dialog.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));
            dialog.setIconImage(Toolkit.getDefaultToolkit().getImage("graphics/kni - ikona16.png"));
            dialog.setBounds(MouseInfo.getPointerInfo().getLocation().x - 12, MouseInfo.getPointerInfo().getLocation().y - 35, parent.getWidth(), parent.getHeight());
            dialog.setSize(parent.getSize());
            dialog.add(this);
            JDesktopPane desktopPanel = ((JInternalFrame) parent).getDesktopPane();
            desktopPanel.remove(parent);
            desktopPanel.repaint();
            dialog.setAlwaysOnTop(true);
            createFormButton();
            setIconArrowDown();
            MainForm.getInstance().removeVisualElement(parent);
            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    close();
                }
            });
            ((JInternalFrame) parent).dispose();
            parent = dialog;
            updateFrameTitle();
            MainForm.getInstance().addVisualElement(dialog);            
            dialog.setVisible(true);
        } else if (parent instanceof JDialog) {
            InternalFrameAllwaysOnTop iFrame = new InternalFrameAllwaysOnTop(bundle.getString("BAR_GRAPH"), true, true);
            iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));
            iFrame.setSize(parent.getSize());
            iFrame.setMinimumSize(getMinimumSize());
            iFrame.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));
            iFrame.add(this);
            JDesktopPane desktopPanel = MainForm.getInstance().getDesktopPanel();
            iFrame.setVisible(true);
            desktopPanel.add(iFrame, 0);
            desktopPanel.repaint();
            try {
                iFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
            createFormButton();
            setIconArrowUp();
            MainForm.getInstance().removeVisualElement(parent);
            iFrame.addInternalFrameListener(new InternalFrameAdapter() {

                @Override
                public void internalFrameClosed(InternalFrameEvent arg0) {
                    close();
                }
            });
            ((JDialog) parent).dispose();
            parent = iFrame;
            updateFrameTitle();
            MainForm.getInstance().addVisualElement(iFrame);
        }
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
        if( sample.getSensor().equals(this.sensor) ) {
            jLabel1.setText(Double.toString(sample.getValue()) + Temperature.getUnitString());
            progressBar.setValue((int)(sample.getValue()));
            repaint();
        }
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
        if( this.sensor.equals(sensor) ) {
            jLabel2.setText(sensor.getName());
            updateFrameTitle();
        }
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceAdd(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
        progressBar.setValue(progressBar.getMinimum());
        jLabel1.setText(bundle.getString("NOT_CONNECTED"));
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
                        setSensor(newSensor);
                        return;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
    }

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

    public void updateFrameTitle() {
        if (sensor != null) {
            if (parent instanceof InternalFrameObservable) {
                ((JInternalFrame) parent).setTitle(bundle.getString("BAR_GRAPH_-_") + sensor.getName());
                ((InternalFrameObservable)parent).notifyTitleChanged(bundle.getString("BAR_GRAPH_-_") + sensor.getName());
            } else if (parent instanceof JDialog) {
                ((JDialog) parent).setTitle(bundle.getString("BAR_GRAPH_-_") + sensor.getName());
            }
        } else {
            if (parent instanceof InternalFrameObservable) {
                ((JInternalFrame) parent).setTitle(bundle.getString("BAR_GRAPH"));
                ((InternalFrameObservable)parent).notifyTitleChanged(bundle.getString("BAR_GRAPH"));
            } else if (parent instanceof JDialog) {
                ((JDialog) parent).setTitle(bundle.getString("BAR_GRAPH"));
            }
        }
    }
}
