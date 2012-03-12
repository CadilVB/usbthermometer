/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Help;

import java.io.Serializable;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author pawelkn
 */
public class HelpNode extends DefaultMutableTreeNode implements Serializable {

    private String title;
    private String contentURL;

    public HelpNode() {
        super();
    }

    public HelpNode(String title, String contentURL) {
        super();

        this.title = title;
        this.contentURL = contentURL;
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return title;
    }
}
