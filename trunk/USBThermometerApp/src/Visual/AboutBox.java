/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AboutBox.java
 *
 * Created on Feb 3, 2012, 11:13:55 AM
 */

package Visual;

import Tools.BrowserControl;
import Tools.CharsetControl;
import Tools.ConfigurationManager;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/**
 *
 * @author Paweł
 */
public class AboutBox extends javax.swing.JDialog {

    public static final String VERSION = "1.0.0";

    private static ResourceBundle bundle;

    private Frame parent;

    /** Creates new form AboutBox */
    public AboutBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;

        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        initComponents();

        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);

        jLabel5.setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("ABOUT")); // NOI18N
        setMinimumSize(new java.awt.Dimension(320, 0));
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setIcon(new ImageIcon("graphics/kni - ikona.png"));
        jLabel1.setAlignmentX(0.5F);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 20, 1));
        getContentPane().add(jLabel1);

        jLabel2.setFont(jLabel2.getFont().deriveFont((float)24));
        jLabel2.setText("USB Thermometer");
        jLabel2.setAlignmentX(0.5F);
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        getContentPane().add(jLabel2);

        jLabel6.setFont(jLabel6.getFont().deriveFont((float)24));
        jLabel6.setText(VERSION);
        jLabel6.setAlignmentX(0.5F);
        jLabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        getContentPane().add(jLabel6);

        jLabel3.setText("Copyright (c) 2012");
        jLabel3.setAlignmentX(0.5F);
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        getContentPane().add(jLabel3);

        jLabel4.setText("KNI Electronics");
        jLabel4.setAlignmentX(0.5F);
        jLabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        getContentPane().add(jLabel4);

        jLabel5.setForeground(new java.awt.Color(0, 51, 255));
        jLabel5.setText("www.kni-electronics.com");
        jLabel5.setAlignmentX(0.5F);
        jLabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel5);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(jTextArea1.getFont().deriveFont((float)12));
        jTextArea1.setRows(5);
        jTextArea1.setText("Some icons by Yusuke Kamiyamane. \nAll rights reserved. Licensed under \na Creative Commons Attribution 3.0 \nLicense. http://p.yusukekamiyamane.com/");
        jTextArea1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTextArea1.setMaximumSize(new java.awt.Dimension(280, 89));
        getContentPane().add(jTextArea1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 1));
        jPanel1.setMinimumSize(new java.awt.Dimension(300, 33));
        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jButton2.setText(bundle.getString("LICENSE")); // NOI18N
        jButton2.setAlignmentX(0.5F);
        jButton2.setMaximumSize(new java.awt.Dimension(87, 23));
        jButton2.setPreferredSize(new java.awt.Dimension(87, 23));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);

        jPanel1.add(jPanel3);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jButton3.setText(bundle.getString("CLOSE")); // NOI18N
        jButton3.setAlignmentX(0.5F);
        jButton3.setMaximumSize(new java.awt.Dimension(87, 23));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        LicenseBox licenseBox = new LicenseBox(parent, true);
        licenseBox.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        BrowserControl.openUrl(jLabel5.getText());
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}