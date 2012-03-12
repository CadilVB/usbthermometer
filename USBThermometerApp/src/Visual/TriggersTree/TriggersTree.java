/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.TriggersTree;

import Visual.Triggers.*;
import Triggers.Trigger;
import Main.Main;
import java.awt.GridLayout;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author pawelkn
 */
public class TriggersTree extends JPanel implements TreeSelectionListener {

    private TriggersConfigurationDialog tcd;

    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected TriggersTreeRender treeRender;
    protected TriggersTreeCellEditor treeCellEditor;
    protected JTree tree;

    public TriggersTree(TriggersConfigurationDialog tcd) {
        super(new GridLayout(1,0));

        this.tcd = tcd;

        rootNode = new DefaultMutableTreeNode("Root Node");
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        treeRender = new TriggersTreeRender();
        treeCellEditor = new TriggersTreeCellEditor(tree, treeRender);

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

        reload();

        for(Trigger trigger : Main.getTriggerManager().getTriggers()) {
            setSelectedTrigger(trigger);
            return;
        }
    }

    public void reload() {
        removeNodes();

        DefaultMutableTreeNode hostNode = new DefaultMutableTreeNode(Main.getLocalhost());
        treeModel.insertNodeInto(hostNode, rootNode, rootNode.getChildCount());

        for(Trigger trigger : Main.getTriggerManager().getTriggers()) {
            DefaultMutableTreeNode triggerNode = new DefaultMutableTreeNode(trigger);
            treeModel.insertNodeInto(triggerNode, hostNode, hostNode.getChildCount());
        }
        expandAll();
    }

    private void removeNodes() {
        for(int i = 0; i < rootNode.getChildCount(); i++ ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(i);
            treeModel.removeNodeFromParent(node);
        }
    }

    public void setSelectedTrigger(Trigger trigger) {
        setSelectedTrigger(trigger, rootNode);
    }

    public boolean setSelectedTrigger(Trigger trigger, DefaultMutableTreeNode node) {
        for(int i = 0; i < node.getChildCount(); i++ ) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)node.getChildAt(i);
            if( setSelectedTrigger(trigger, childNode) ) {
                return true;
            } else if( childNode.getUserObject() == trigger ) {
                tree.setSelectionPath( new TreePath( childNode.getPath() ) );
                return true;
            }
        }
        return false;
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
    public void valueChanged(TreeSelectionEvent e) {
        TreePath treePath = e.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (treePath.getLastPathComponent());
        if (node.getUserObject() instanceof Trigger) {
            Trigger trigger = (Trigger) (node.getUserObject());
            tcd.setVisibleTrigger(trigger);
        } else {
            tcd.setVisibleTrigger(null);
        }
    }
}
