/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 *
 * @author pawelkn
 */
public class InternalFrameMenuItem extends JMenuItem {

    private final InternalFrameObservable ifo;
    private static final ImageIcon icon = new ImageIcon("graphics/plus-small.png");

    public InternalFrameMenuItem(final InternalFrameObservable ifo) {
        super(ifo.getTitle());
        this.ifo = ifo;

        addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setSelected();
            }
        });

        ifo.addInternalFrameObserver( new InternalFrameObserver() {
            @Override
            public void titleChanged(String text) {
                titleChange(text);
            }            
        });

        ifo.addInternalFrameListener( new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent arg0) {
                setIcon();
            }
            @Override
            public void internalFrameDeactivated(InternalFrameEvent arg0) {
                removeIcon();
            }
            @Override
            public void internalFrameClosed(InternalFrameEvent arg0) {
                remove();
            }
        });
    }

    private void setSelected() {
        try {
            ifo.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    private void remove() {
        if( getParent() != null ) {
            getParent().remove(this);
        }
    }

    private void setIcon() {
        setIcon(icon);
    }

    private void removeIcon() {
        setIcon(null);
    }

    public void titleChange(String text) {
        setText(text);
    }
}
