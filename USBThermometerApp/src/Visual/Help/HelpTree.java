/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Help;

import Tools.CharsetControl;
import Tools.ConfigurationManager;
import java.awt.GridLayout;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
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
public class HelpTree extends JPanel implements TreeSelectionListener, TreeExpansionListener  {

    private HelpCtx helpCtx;

    protected HelpNode rootNode;
    protected DefaultTreeModel treeModel;
    protected HelpTreeRender treeRender;
    protected HelpTreeCellEditor treeCellEditor;
    protected JTree tree;

    private HelpNode currentNode;
    
    private static Locale locale;
    private static ResourceBundle bundle;

    public HelpTree(HelpCtx helpCtx) {
        super(new GridLayout(1,0));
        
        locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        this.helpCtx = helpCtx;

        rootNode = new HelpNode("Root Node", "Root Node");
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        treeRender = new HelpTreeRender();
        treeCellEditor = new HelpTreeCellEditor(tree, treeRender);
        
        tree.addTreeExpansionListener(this);

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        tree.setDragEnabled(false);
        tree.setEditable(false);
        tree.addTreeSelectionListener(this);
        tree.setCellEditor(treeCellEditor);

        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.setCellRenderer(treeRender);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);

        reload();
    }

    public void reload() {
        removeNodes();

        try {
            XMLDecoder decoder =
                    new XMLDecoder(new BufferedInputStream(
                    new FileInputStream("help/help_" + locale.getLanguage() + ".xml")));
            HelpNode node = (HelpNode) decoder.readObject();
            treeModel.insertNodeInto(node, rootNode, rootNode.getChildCount());
            decoder.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }

        expandRoot();
    }

    private void removeNodes() {
        for(int i = 0; i < rootNode.getChildCount(); i++ ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(i);
            treeModel.removeNodeFromParent(node);
        }
    }

    public HelpNode findHelpNode(String contentURL) {
        if(contentURL == null) {
            return null;
        }
        return findHelpNode( contentURL, rootNode );
    }

    private HelpNode findHelpNode(String contentURL, HelpNode node) {
        for(int i = 0; i < node.getChildCount(); i++ ) {
            HelpNode childNode =  (HelpNode)node.getChildAt(i);
            if( childNode.getContentURL().indexOf(contentURL) != -1 ) {
                return childNode;
            } else {
                HelpNode resultNode = findHelpNode(contentURL, childNode);
                if( resultNode != null ) {
                    return resultNode;
                }
            }
        }
        return null;
    }

    public void setSelectedNode(HelpNode node) {
        setSelectedNode(node, rootNode);
        currentNode = node;
    }

    public HelpNode getSelectedNode() {
        return currentNode;
    }

    private boolean setSelectedNode(HelpNode node, HelpNode parentNode) {
        for(int i = 0; i < parentNode.getChildCount(); i++ ) {
            HelpNode childNode = (HelpNode)parentNode.getChildAt(i);
            if( setSelectedNode(node, childNode) ) {
                return true;
            } else if( childNode == node ) {
                tree.setSelectionPath( new TreePath( childNode.getPath() ) );
                return true;
            }
        }
        return false;
    }

    public void expandRoot() {        
        TreePath rootPath = new TreePath(rootNode);
        tree.expandPath(rootPath);
        for(int i = 0; i < rootNode.getChildCount(); i++) {
            TreeNode node = (TreeNode)rootNode.getChildAt(i);
            tree.expandPath(rootPath.pathByAddingChild(node));
        }
    }

    public void expandAll() {
        expandAll(new TreePath(rootNode), true);
    }
    private void expandAll(TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(path, expand);
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
        Object pathComponent = treePath.getLastPathComponent();
        if(pathComponent instanceof HelpNode) {
            HelpNode node = (HelpNode) (pathComponent);
            helpCtx.setPageNode(node);
            currentNode = node;
        }
    }

    @Override
    public void treeExpanded(TreeExpansionEvent e) {
        Object pathComponent = e.getPath().getLastPathComponent();
        if(pathComponent instanceof HelpNode) {
            HelpNode node = (HelpNode) (pathComponent);
            tree.expandPath(new TreePath(node));
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent e) {
        Object pathComponent = e.getPath().getLastPathComponent();
        if(pathComponent instanceof HelpNode) {
            HelpNode node = (HelpNode) (pathComponent);
            tree.collapsePath(new TreePath(node));
        }        
    }
}
