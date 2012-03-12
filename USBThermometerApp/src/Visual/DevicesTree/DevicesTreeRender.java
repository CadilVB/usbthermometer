/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.DevicesTree;

import USBThermometerLib.Sensor;
import USBThermometerLib.USBThermometerDevice;
import USBThermometerLib.Device;
import Engine.*;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Paweł
 */
public class DevicesTreeRender extends DefaultTreeCellRenderer {
    protected static Icon localhostIcon;
    protected static Icon usbThermometerIcon;
    protected static Icon thermometerIcon;
    protected static Icon thermometerOffIcon;

    public DevicesTreeRender() {
        localhostIcon = new ImageIcon("graphics/computer-off.png");
        usbThermometerIcon = new ImageIcon("graphics/usb-flash-drive-logo.png");
        thermometerIcon = new ImageIcon("graphics/thermometer.png");
        thermometerOffIcon = new ImageIcon("graphics/thermometer_off.png");
    }

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        if (isLocalhost(value)) {
            setIcon(localhostIcon);
        } else if (isUSBThermometer(value)) {
            setIcon(usbThermometerIcon);
        } else if (isActiveSensor(value)) {
            setIcon(thermometerIcon);
        } else if (isOffSensor(value)) {
            setIcon(thermometerOffIcon);
        } else {
            setToolTipText(null); //no tool tip
        }

        return this;
    }

    protected static boolean isLocalhost(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if( node.getUserObject() instanceof Host ) {
            return true;
        }
        return false;
    }

    protected static boolean isUSBThermometer(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if( node.getUserObject() instanceof USBThermometerDevice ) {
            return true;
        }
        return false;
    }

    protected static boolean isActiveSensor(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)(node.getParent());
        if( parentNode != null ) {
            if( parentNode.getUserObject() instanceof Device ) {
                if( node.getUserObject() instanceof Sensor ) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static boolean isOffSensor(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)(node.getParent());
        if( parentNode != null ) {
            if( parentNode.getUserObject() instanceof Host ) {
                if( node.getUserObject() instanceof Sensor ) {
                    return true;
                }
            }
        }
        return false;
    }
}
