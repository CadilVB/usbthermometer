/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tools;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Paweł
 */
public class TrayIconMouseAdapter extends MouseAdapter {

    boolean hideClick = false;
    JDialog dummy;
    final JPopupMenu pop_up;

    public TrayIconMouseAdapter(final JPopupMenu pop_up) {
        this.pop_up = pop_up;

        dummy = new JDialog();
        dummy.setLocation(-1000, -1000);
        dummy.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        pop_up.setVisible(false);
                        dummy.setVisible(false);
                        hideClick = false;
                    }
                }).start();
            }
        });
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger() && !hideClick) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Dimension scrnsize = toolkit.getScreenSize();
                    int popUpHeight = pop_up.getHeight();
                    int popUpWidth = pop_up.getWidth();
                    int x = e.getX();
                    int y = e.getY();
                    if (scrnsize.getHeight() - y < popUpHeight) {
                        y -= popUpHeight;
                    }
                    if (scrnsize.getWidth() - x < popUpWidth) {
                        x -= popUpWidth;
                    }
                    pop_up.setLocation(x, y);
                }
            });
            dummy.setVisible(true);
            pop_up.setInvoker(dummy);
            pop_up.setVisible(true);
            hideClick = true;
        } else {
            hideClick = false;
            dummy.setVisible(false);
        }
    }
}
