/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.DevicesTree;

import USBThermometerLib.Sensor;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author pawelkn
 */
public class DevicesTreeCellEditor extends DefaultTreeCellEditor {

    Object orginalObject;

    public DevicesTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected,
            boolean expanded,
            boolean leaf, int row) {

        Component component = super.getTreeCellEditorComponent(tree, value, leaf, leaf, leaf, row);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (value);
        orginalObject = node.getUserObject();        

        if( DevicesTreeRender.isActiveSensor(value) ) {
            editingIcon = DevicesTreeRender.thermometerIcon;
        } else if( DevicesTreeRender.isOffSensor(value) ) {
            editingIcon = DevicesTreeRender.thermometerOffIcon;
        }

        return component;
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        TreePath treePath = tree.getSelectionPath();
        if( treePath != null ) {
            DefaultMutableTreeNode selectedNode = ((DefaultMutableTreeNode) (treePath.getLastPathComponent()));
            if (selectedNode.getUserObject() instanceof Sensor) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public Object getOryginalObject() {
        return orginalObject;
    }
}
