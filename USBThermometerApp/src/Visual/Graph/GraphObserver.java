/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Graph;

import java.util.Date;

/**
 *
 * @author pawelkn
 */
public interface GraphObserver {

    public void pointedDateChanged(Date date, Double value);
}
