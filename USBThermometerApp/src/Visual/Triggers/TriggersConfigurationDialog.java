/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TriggersConfigurationPanel.java
 *
 * Created on 2012-01-18, 12:33:05
 */

package Visual.Triggers;

import Main.Main;
import Tools.CharsetControl;
import Tools.ConfigurationManager;
import Tools.ToolBox;
import Triggers.*;
import USBThermometerLib.Device;
import USBThermometerLib.Sensor;
import Visual.Tools.DocumentAdapter;
import Visual.TriggersTree.TriggersTree;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author pawelkn
 */
public class TriggersConfigurationDialog extends javax.swing.JDialog {
    private static ResourceBundle bundle;

    public TriggersTree triggersTree;
    public Trigger visibleTrigger;

    private SaveDocumentAdapter saveDocumentAdapter = new SaveDocumentAdapter();
    private SaveItemStateChangedAdapter saveItemStateChangedAdapter = new SaveItemStateChangedAdapter();

    /** Creates new form TriggersConfigurationPanel */
    public TriggersConfigurationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);    
        
        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());        
        
        initComponents();

        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);

        enabledCheckBox.setVisible(false);
        jPanel5.setVisible(false);
        setSaveActionListener(jPanel4, true);
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);

        triggersTree = new TriggersTree(this);
        jPanel1.add(triggersTree);

        triggersTree.setSelectedTrigger(visibleTrigger);

        thresholdTextField.getDocument().addDocumentListener( new DocumentAdapter() {
            @Override
            public void update() {
                ToolBox.tryParseDouble(thresholdTextField);
            }
        });

        hysteresisTextField.getDocument().addDocumentListener( new DocumentAdapter() {
            @Override
            public void update() {
                ToolBox.tryParseDoublePositive(hysteresisTextField);
            }
        });
    }

    public void setVisibleTrigger(Trigger trigger) {
        if (trigger != null) {
            visibleTrigger = trigger;

            jPanel3.removeAll();
            jPanel3.add(trigger.getConfigurationPanel());

            enabledCheckBox.setSelected(trigger.isEnabled());

            nameTextField.setText(trigger.getName());
            Sensor sensor = Main.findSensorByStringId(trigger.getSensorId());
            if (sensor != null) {
                sensorTextField.setText(sensor.getName());
            } else {
                sensorTextField.setText(null);
            }
            thresholdTextField.setText(Double.toString(trigger.getThreshold()));
            thresholdDirectionComboBox1.setSelectedIndex(trigger.getThresholdDirection());
            hysteresisTextField.setText(Double.toString(trigger.getHysteresis()));

            enabledCheckBox.setVisible(true);
            jPanel5.setVisible(true);
            deleteButton.setEnabled(true);
            saveButton.setEnabled(false);

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setSaveActionListener(jPanel3, true);
                }
            });

            validate();
            repaint();
        } else {
            visibleTrigger = null;

            enabledCheckBox.setVisible(false);
            jPanel5.setVisible(false);

            setSaveActionListener(jPanel3, false);
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);

            validate();
            repaint();
        }
    }
    
    private void setSaveActionListener(Component c, boolean state) {
        if(c != null) {
            if( c instanceof Container ) {
                for(Component cc : ((Container)c).getComponents()) {
                    setSaveActionListener(cc, state);
                }
            }
            if( c instanceof JTextComponent ) {
                Document doc = ((JTextComponent)c).getDocument();
                if( state ) {
                    doc.addDocumentListener( saveDocumentAdapter );
                } else {
                    doc.removeDocumentListener( saveDocumentAdapter );
                }
            } else if( c instanceof AbstractButton ) {
                AbstractButton ab = (AbstractButton)c;
                if( state ) {
                    ab.addItemListener(saveItemStateChangedAdapter);
                } else {
                    ab.removeItemListener(saveItemStateChangedAdapter);
                }
            } else if( c instanceof JComboBox ) {
                JComboBox cb = (JComboBox)c;
                if( state ) {
                    cb.addItemListener(saveItemStateChangedAdapter);
                } else {
                    cb.removeItemListener(saveItemStateChangedAdapter);
                }
            }
        }
    }

    private class SaveDocumentAdapter extends DocumentAdapter {
        @Override
        public void update() {
            saveButton.setEnabled(true);
        }
    }

    private class SaveItemStateChangedAdapter implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            saveButton.setEnabled(true);
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

        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        enabledCheckBox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        sensorTextField = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        thresholdTextField = new javax.swing.JTextField();
        thresholdDirectionComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        hysteresisTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("TRIGGERS SETTINGS")); // NOI18N

        saveButton.setText(bundle.getString("SAVE")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(bundle.getString("DELETE")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        newButton.setText(bundle.getString("NEW")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(200);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridLayout(1, 1));
        jSplitPane1.setLeftComponent(jPanel1);

        enabledCheckBox.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        enabledCheckBox.setText(bundle.getString("ENABLED")); // NOI18N
        enabledCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enabledCheckBoxStateChanged(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("TRIGGER SETTINGS"))); // NOI18N
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 200));

        jLabel1.setText(bundle.getString("NAME:")); // NOI18N

        jLabel2.setText(bundle.getString("SENSOR:")); // NOI18N

        sensorTextField.setEditable(false);

        jButton4.setText("...");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("THRESHOLD:")); // NOI18N

        thresholdDirectionComboBox1.setModel(new DefaultComboBoxModel(
            new String[] { 
                bundle.getString("TRIGGER BELLOW THRESHOLD"),
                bundle.getString("TRIGGER OVER THRESHOLD") }));

    jLabel4.setText(bundle.getString("HYSTERESIS:")); // NOI18N

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2)
                .addComponent(jLabel1)
                .addComponent(jLabel3))
            .addGap(18, 18, 18)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addComponent(sensorTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(thresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(thresholdDirectionComboBox1, 0, 1, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(hysteresisTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(sensorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(jButton4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(thresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel3)
                .addComponent(thresholdDirectionComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(hysteresisTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel4))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ADDITIONAL SETTINGS"))); // NOI18N
    jPanel3.setLayout(new java.awt.GridLayout(1, 1));

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(enabledCheckBox)
            .addContainerGap(370, Short.MAX_VALUE))
        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(enabledCheckBox)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jSplitPane1.setRightComponent(jPanel4);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap(446, Short.MAX_VALUE)
            .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(jSplitPane1)
            .addGap(11, 11, 11)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(saveButton)
                .addComponent(deleteButton)
                .addComponent(newButton))
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enabledCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enabledCheckBoxStateChanged
        //setControlState(jPanel5, enabledCheckBox.isSelected());
    }//GEN-LAST:event_enabledCheckBoxStateChanged

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        String[] possibilities = new String[]{
            bundle.getString("SMTP TRIGGER"),
            bundle.getString("RS232 TRIGGER"),
            bundle.getString("NOTIFICATION TRIGGER"),
            bundle.getString("TCP/IP TRIGGER"),
            bundle.getString("UDP TRIGGER"),
            bundle.getString("USER TRIGGER")
        };

        String s = (String) JOptionPane.showInputDialog(
                this,
                bundle.getString("SELECT TRIGGER TYPE"),
                bundle.getString("NEW TRIGGER"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                null);

        Trigger trigger = null;

        if (bundle.getString("SMTP TRIGGER").equals(s)) {
            trigger = new SmtpTrigger();            
        } else if ( bundle.getString("RS232 TRIGGER").equals(s)) {
            trigger = new RS232Trigger();
        } else if ( bundle.getString("NOTIFICATION TRIGGER").equals(s)) {
            trigger = new NotificationTrigger();
        } else if ( bundle.getString("TCP/IP TRIGGER").equals(s)) {
            trigger = new TCPIPTrigger();
        } else if ( bundle.getString("UDP TRIGGER").equals(s)) {
            trigger = new UDPTrigger();
        } else if ( bundle.getString("USER TRIGGER").equals(s)) {
            trigger = new CustomTrigger();
        }

        if (trigger != null) {
            trigger.setName(s);
            TriggersManager tm = Main.getTriggerManager();            
            
            tm.addTrigger(trigger);
            trigger.save();
            triggersTree.reload();
            triggersTree.setSelectedTrigger(trigger);
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        boolean save = true;
        if( "".equals(sensorTextField.getText()) ) {
            int result = JOptionPane.showConfirmDialog(this,
                bundle.getString("SENSOR FIELD IS EMPTY. SAVE NEVERTHELESS?"),
                bundle.getString("SENSOR FIELD WARNING"),
                JOptionPane.OK_CANCEL_OPTION);
            
            if( result == JOptionPane.CANCEL_OPTION ) {
                save = false;
            } 
        }
        
        if( save ) { 
            visibleTrigger.setName(nameTextField.getText());
            Sensor sensor = Main.findSensorByName( sensorTextField.getText() );
            if( sensor != null ) {
                visibleTrigger.setSensorId( sensor.getStringId() );
            }

            try {
                visibleTrigger.setThreshold(Double.parseDouble( thresholdTextField.getText() ));
            } catch(Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }

            visibleTrigger.setThresholdDirection( thresholdDirectionComboBox1.getSelectedIndex() );

            try {
                visibleTrigger.setHysteresis(Double.parseDouble( hysteresisTextField.getText() ));
            } catch(Exception ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }

            visibleTrigger.setEnabled( enabledCheckBox.isSelected() );
            visibleTrigger.updateConfiguration();
            visibleTrigger.save();

            saveButton.setEnabled(false);
            triggersTree.reload();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        String message = bundle.getString("ARE YOU SURE YOU WANT TO DELETE THE TRIGGER");
        int answer = JOptionPane.showConfirmDialog(this, message);
        if (answer == JOptionPane.YES_OPTION) {
            TriggersManager tm = visibleTrigger.getTriggersManager();
            if (tm != null) {
                tm.removeTrigger(visibleTrigger);
                tm.save();
            }

            triggersTree.reload();
            setVisibleTrigger(null);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        List possibilities = new ArrayList();
        for(Device device : Main.getLocalhost().getDevices()) {
            for(Sensor sensor : device.getSensors()) {
                possibilities.add(sensor.getName());
            }
        }

        String s = (String)JOptionPane.showInputDialog(
                            this,
                            bundle.getString("SELECT SENSOR"),
                            bundle.getString("SENSOR SELECTOR"),
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            possibilities.toArray(),
                            null);

        sensorTextField.setText(s);
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JTextField hysteresisTextField;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField sensorTextField;
    private javax.swing.JComboBox thresholdDirectionComboBox1;
    private javax.swing.JTextField thresholdTextField;
    // End of variables declaration//GEN-END:variables

}
