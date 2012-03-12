/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;

/**
 *
 * @author pawelkn
 */
public class InternalFrameObservable extends JInternalFrame {

    private List<InternalFrameObserver> ifos = new ArrayList<InternalFrameObserver>();

    public InternalFrameObservable(String title) {
        super(title);
    }

    public InternalFrameObservable(String title, boolean arg0, boolean arg1) {
        super(title, arg0, arg1);
    }

    public void addInternalFrameObserver(InternalFrameObserver ifo) {
        ifos.add(ifo);
    }

    public void removeInternalFrameObserver(InternalFrameObserver ifo) {
        ifos.remove(ifo);
    }

    public void notifyTitleChanged(String text) {
        for(InternalFrameObserver ifo : ifos) {
            ifo.titleChanged(text);
        }
    }
}
