/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RS232TriggerConfigurationPanel.java
 *
 * Created on 2012-01-19, 15:19:11
 */

package Visual.Triggers;

import Tools.CharsetControl;
import Tools.ConfigurationManager;
import Tools.ToolBox;
import Visual.Tools.DataArea;
import Visual.Tools.DocumentAdapter;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author pawelkn
 */
public class RS232TriggerConfigurationPanel extends javax.swing.JPanel {
    private static ResourceBundle bundle;

    private DataArea dataArea;

    /** Creates new form RS232TriggerConfigurationPanel */
    public RS232TriggerConfigurationPanel() {
        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());        
        
        initComponents();

        dataArea = new DataArea();
        dataArea.setEnabled(false);
        jScrollPane1.setViewportView(dataArea);

        jTextField1.getDocument().addDocumentListener( new DocumentAdapter() {
            @Override
            public void update() {
                ToolBox.tryParseIntPositive(jTextField1);
            }
        });
    }

    public void setComPort(String comPort) {
        comPortTextField.setText(comPort);
    }

    public void setDtrState(int state) {
        dtrStateComboBox.setSelectedIndex(state);
    }

    public void setRtsState(int state) {
        rtsStateComboBox.setSelectedIndex(state);
    }

    public void setSendCommand(boolean state) {
        sendCommandCheckBox.setSelected(state);
    }

    public void setCommand(byte[] command) {
        dataArea.setData(command);
    }

    public void setBaudRate(int baud) {
        jTextField1.setText(String.valueOf(baud));
    }

    public void setBits(int bits) {
        jComboBox2.setSelectedIndex(bits);
    }

    public void setStopBits(int stopBits) {
        jComboBox1.setSelectedIndex(stopBits);
    }

    public void setParity(int parity) {
        jComboBox3.setSelectedIndex(parity);
    }

    public String getComPort() {
        return comPortTextField.getText();
    }

    public int getDtrState() {
        return dtrStateComboBox.getSelectedIndex();
    }

    public int getRtsState() {
        return rtsStateComboBox.getSelectedIndex();
    }

    public boolean getSendCommand() {
        return sendCommandCheckBox.isSelected();
    }

    public byte[] getCommand() {
        return dataArea.getData();
    }

    public int getBaudRate() {
        try {
            return Integer.parseInt(jTextField1.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    public int getBits() {
        return jComboBox2.getSelectedIndex();
    }

    public int getStopBits() {
        return jComboBox1.getSelectedIndex();
    }

    public int getParity() {
        return jComboBox3.getSelectedIndex();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        comPortTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rtsStateComboBox = new javax.swing.JComboBox();
        dtrStateComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        commandModeComboBox = new javax.swing.JComboBox();
        sendCommandCheckBox = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();

        jLabel1.setText(bundle.getString("COM PORT:")); // NOI18N

        comPortTextField.setText("COM1");

        jLabel2.setText(bundle.getString("RTS PIN:")); // NOI18N

        rtsStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Do not change", "Set to '0'", "Set to '1'" }));

        dtrStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Do not change", "Set to '0'", "Set to '1'" }));

        jLabel3.setText(bundle.getString("DTR PIN:")); // NOI18N

        commandModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ASCI", "HEX" }));
        commandModeComboBox.setEnabled(false);
        commandModeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                commandModeComboBoxItemStateChanged(evt);
            }
        });

        sendCommandCheckBox.setText(bundle.getString("SEND A COMMAND:")); // NOI18N
        sendCommandCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sendCommandCheckBoxStateChanged(evt);
            }
        });

        jTextField1.setText("9600");

        jLabel4.setText(bundle.getString("BAUD RATE:")); // NOI18N

        jLabel5.setText(bundle.getString("STOP BITS:")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "1,5", "2" }));
        jComboBox1.setSelectedIndex(2);

        jLabel6.setText(bundle.getString("BITS:")); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5", "6", "7", "8" }));

        jLabel7.setText(bundle.getString("PARITY:")); // NOI18N

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EVEN", "MARK", "NONE", "ODD", "SPACE" }));
        jComboBox3.setSelectedIndex(2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comPortTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, 0, 31, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, 41, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, 0, 65, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rtsStateComboBox, 0, 123, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dtrStateComboBox, 0, 153, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(sendCommandCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                        .addComponent(commandModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rtsStateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(dtrStateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commandModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendCommandCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sendCommandCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sendCommandCheckBoxStateChanged
        dataArea.setEnabled(sendCommandCheckBox.isSelected());
        commandModeComboBox.setEnabled(sendCommandCheckBox.isSelected());
    }//GEN-LAST:event_sendCommandCheckBoxStateChanged

    private void commandModeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_commandModeComboBoxItemStateChanged
        if( "ASCI".equals( commandModeComboBox.getSelectedItem().toString() ) ) {
            dataArea.setDataState(DataArea.DATASTATE_ASCI);
        } else {
            dataArea.setDataState(DataArea.DATASTATE_HEX);
        }
    }//GEN-LAST:event_commandModeComboBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField comPortTextField;
    private javax.swing.JComboBox commandModeComboBox;
    private javax.swing.JComboBox dtrStateComboBox;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox rtsStateComboBox;
    private javax.swing.JCheckBox sendCommandCheckBox;
    // End of variables declaration//GEN-END:variables

}
