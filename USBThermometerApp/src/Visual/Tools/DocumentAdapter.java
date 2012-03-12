/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Tools;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author pawelkn
 */
public abstract class DocumentAdapter implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        update();
    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        update();
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        update();
    }

    public abstract void update();
}
