/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.DevicesTree;

import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.USBThermometerDevice;
import USBThermometerLib.Device;
import Engine.*;

import Main.Main;
import Visual.SensorStatusLabel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Paweł
 */
public class DevicesTree extends JPanel implements HostObserver, TreeModelListener, TreeSelectionListener  {

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected DevicesTreeRender treeRender;
    protected DevicesTreeCellEditor treeCellEditor;
    protected JTree tree;

    public DevicesTree() {
        super(new GridLayout(1,0));

        rootNode = new DefaultMutableTreeNode("Root Node");

        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(this);

        tree = new JTree(treeModel);
        treeRender = new DevicesTreeRender();
        treeCellEditor = new DevicesTreeCellEditor(tree, treeRender);

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(false);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setEditable(true);
        tree.addTreeSelectionListener(this);
        tree.setCellEditor(treeCellEditor);
        
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(treeRender);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);

        for(Device device : Main.getLocalhost().getDevices()) {
            notiffyDeviceAdd(Main.getLocalhost(),device);
        }
        Main.getLocalhost().addHostObserver(this);
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
        DefaultMutableTreeNode hostNode = findNode(rootNode, host);

        if( device instanceof USBThermometerDevice  ) {
            DefaultMutableTreeNode deviceNode = findNode(hostNode, device);
            removeAllSensorNodes(deviceNode);
            for(Sensor sensor : device.getSensors()) {
                DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensor);
                treeModel.insertNodeInto(sensorNode, deviceNode, deviceNode.getChildCount());
            }
            treeModel.reload();
            expandAll();
        }
        else if( device instanceof NullDevice ) {
            removeAllSensorNodes(hostNode);
            for(Sensor sensor : device.getSensors()) {
                DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensor);
                treeModel.insertNodeInto(sensorNode, hostNode, hostNode.getChildCount());
            }
            treeModel.reload();
            expandAll();
        }
    }

    @Override
    public void notiffyDeviceAdd(Host host, Device device) {
        DefaultMutableTreeNode hostNode = findNode(rootNode, host);

        if( device instanceof USBThermometerDevice  ) {
            DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device);
            treeModel.insertNodeInto(deviceNode, hostNode, 0);
            for(Sensor sensor : device.getSensors()) {
                DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensor);
                treeModel.insertNodeInto(sensorNode, deviceNode, deviceNode.getChildCount());
            }
            treeModel.reload();
            expandAll();
        }
        else if( device instanceof NullDevice  ) {
            for(Sensor sensor : device.getSensors()) {
                DefaultMutableTreeNode sensorNode = new DefaultMutableTreeNode(sensor);
                treeModel.insertNodeInto(sensorNode, hostNode, hostNode.getChildCount());
            }
            treeModel.reload();
            expandAll();
        }
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
        DefaultMutableTreeNode hostNode = findNode(rootNode, host);

        if( device instanceof USBThermometerDevice  ) {
            DefaultMutableTreeNode deviceNode = findNode(hostNode, device);
            removeAllSensorNodes(deviceNode);
            deviceNode.removeFromParent();
            treeModel.reload();
            expandAll();
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode parentNode, Object obj) {
        DefaultMutableTreeNode node = null;
        if (parentNode.getChildCount() > 0) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (node.getUserObject().equals(obj)) {
                    break;
                }                
            }
        }
        if (node == null) {
            node = new DefaultMutableTreeNode(obj);
            treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
        }
        return node;
    }

    private void removeAllSensorNodes(DefaultMutableTreeNode parentNode) {
        if (parentNode.getChildCount() > 0) {
            List<DefaultMutableTreeNode> nodesToDelete = new ArrayList<DefaultMutableTreeNode>();
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if (node.getUserObject() instanceof Sensor) {
                    nodesToDelete.add(node);
                }
            }
            for (DefaultMutableTreeNode nodeToDelete : nodesToDelete) {
                nodeToDelete.removeFromParent();
            }
        }
    }

    public void expandAll() {
        TreeNode root = (TreeNode)tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), true);
    }
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    @Override
    public void treeNodesChanged(TreeModelEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(arg0.getChildren()[0]);
        String newName = node.getUserObject().toString();
        
        if( treeCellEditor.getOryginalObject() instanceof Sensor ) {
            Sensor sensor = (Sensor)(treeCellEditor.getOryginalObject());
            if( sensor != null ) {
                sensor.setName(newName);
                Main.getLocalhost().updateSensor(sensor);
                node.setUserObject(sensor);
                tree.repaint();
            }
        }
    }

    @Override
    public void treeNodesInserted(TreeModelEvent arg0) {
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent arg0) {
    }

    @Override
    public void treeStructureChanged(TreeModelEvent arg0) {
    }

    @Override
    public void valueChanged(TreeSelectionEvent arg0) {
        TreePath treePath = arg0.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)(treePath.getLastPathComponent());
        if( node.getUserObject() instanceof Sensor ) {
            Sensor sensor = (Sensor)(node.getUserObject());
            SensorStatusLabel ssl = SensorStatusLabel.getInstance();
            ssl.setSensor(sensor);
        } else {
            SensorStatusLabel ssl = SensorStatusLabel.getInstance();
            ssl.removeSensor();
        }
    }
}
