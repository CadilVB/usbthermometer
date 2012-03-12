/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.TriggersTree;

import Engine.*;

import Triggers.Trigger;
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
public class TriggersTreeRender extends DefaultTreeCellRenderer {
    protected static Icon localhostIcon;
    protected static Icon activeTriggerIcon;
    protected static Icon notActiveTriggerIcon;

    public TriggersTreeRender() {
        localhostIcon = new ImageIcon("graphics/computer-off.png");
        activeTriggerIcon = new ImageIcon("graphics/tick-circle.png");
        notActiveTriggerIcon = new ImageIcon("graphics/tick-white.png");
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
        } else if (isActiveTrigger(value)) {
            setIcon(activeTriggerIcon);
        } else if (isNotActiveTrigger(value)) {
            setIcon(notActiveTriggerIcon);
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

    protected static boolean isActiveTrigger(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if( ( node.getUserObject() instanceof Trigger ) &&
            ( ((Trigger)node.getUserObject()).isEnabled() ) ) {
            return true;
        }
        return false;
    }

    protected static boolean isNotActiveTrigger(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if( ( node.getUserObject() instanceof Trigger ) &&
            ( !((Trigger)node.getUserObject()).isEnabled() ) ) {
            return true;
        }
        return false;
    }
}
