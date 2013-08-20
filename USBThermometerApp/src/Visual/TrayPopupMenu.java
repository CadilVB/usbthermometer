/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author pawel
 */
public class TrayPopupMenu extends JPopupMenu {
    
    public TrayPopupMenu() {
        super();
    }
    
    
    private class CloseMouseListener extends MouseAdapter {
        @Override
        public void mouseExited(MouseEvent e) {
            // MenuSelectionManager disarms the old one and arms the new item in one event of the EventDispatchingThread.
            // So, after that event we can be sure MenuSelectionManager has completed his job and the new item (or none) is selected.
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!isAnItemSelected()) {
                        cancel();
                    }
                }
            });
        }
    }

    private MouseListener closeMouseListener = new CloseMouseListener();

    private boolean isAnItemSelected() {
        for (Component comp : TrayPopupMenu.this.getComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                if (item.isArmed()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public JMenuItem add(JMenuItem menuItem) {
        menuItem.addMouseListener(closeMouseListener);
        return super.add(menuItem);
    }

    @Override
    public void remove(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (pos > getComponentCount() - 1) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }

        Component comp = getComponent(pos);
        if (comp instanceof JMenuItem) {
            comp.removeMouseListener(closeMouseListener);
        }

        super.remove(pos);
    }

    private void cancel() {
        firePopupMenuCanceled();
        setVisible(false);
    }    
}
