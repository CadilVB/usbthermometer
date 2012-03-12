/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserTriggerConfigurationPanel.java
 *
 * Created on 2012-01-23, 10:37:59
 */

package Visual.Triggers;

import Tools.CharsetControl;
import Tools.ConfigurationManager;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author pawelkn
 */
public class CustomTriggerConfigurationPanel extends javax.swing.JPanel {
    private static ResourceBundle bundle;

    /** Creates new form UserTriggerConfigurationPanel */
    public CustomTriggerConfigurationPanel() {
        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());        
        
        initComponents();
    }

    public String getJarFilePath() {
        return jTextField1.getText();
    }

    public void setJarFilePath(String classPath) {
        jTextField1.setText(classPath);
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
        jTextField1 = new javax.swing.JTextField();

        jLabel1.setText(bundle.getString("SHELL_COMMAND")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 307, Short.MAX_VALUE))
                    .addComponent(jTextField1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
