/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Tools;

/**
 *
 * @author pawelkn
 */
public class InternalFrameAllwaysOnTop extends InternalFrameObservable {

    public InternalFrameAllwaysOnTop(String title) {
        super(title);
    }

    public InternalFrameAllwaysOnTop(String title, boolean arg0, boolean arg1) {
        super(title, arg0, arg1);
    }

    public boolean isSelected() {
        return true;
    }
}
