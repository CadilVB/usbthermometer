/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import Tools.ConfigurationManager;
import Tools.CharsetControl;
import java.awt.Component;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JTabbedPane;

/**
 *
 * @author Paweł
 */
public class InternationalizedTabbedPane extends JTabbedPane {

    private static ResourceBundle bundle;

    public InternationalizedTabbedPane() {
        super();

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());
    }

    @Override
    public void addTab(String title, Component component) {
        super.addTab(bundle.getString(title), component);
    }

}
