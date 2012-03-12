/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Help;


import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author Paweł
 */
public class HelpTreeRender extends DefaultTreeCellRenderer {
    protected static Icon bookIcon;
    protected static Icon documentIcon;
    protected static Icon bookOpenIcon;

    public HelpTreeRender() {
        bookIcon = new ImageIcon("graphics/book.png");
        documentIcon = new ImageIcon("graphics/document.png");
        bookOpenIcon = new ImageIcon("graphics/book-open.png");
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

        if (hasChilds(value)) {
            HelpNode helpNode = (HelpNode)value;
            if( tree.isExpanded(new TreePath(helpNode)) ) {
                setIcon(bookOpenIcon);
            } else {
                setIcon(bookIcon);
            }
        } else {
            setIcon(documentIcon);
        }

        return this;
    }

    protected static boolean hasChilds(Object value) {
        if( value instanceof HelpNode ) {
            HelpNode helpNode = (HelpNode)value;
            if( helpNode.getChildCount() > 0 ) {
                return true;
            }
        }
        return false;
    }
}
