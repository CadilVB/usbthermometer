/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Help;

import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author pawelkn
 */
public class HelpTreeCellEditor extends DefaultTreeCellEditor {

    public HelpTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        return false;
    }
}
