/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Graph;

import USBThermometerLib.Temperature;
import Visual.Tools.GraphObserver;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author pawelkn
 */
public class GraphStatusPanel extends JToolBar implements GraphObserver {

    private final JLabel dateLabel;
    private final JLabel valueLabel;
    private final Graph graph;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GraphStatusPanel(Graph graph) {
        super();

        this.graph = graph;
        graph.addGraphObserver(this);

        dateLabel = new JLabel();
        valueLabel = new JLabel();

        setFloatable(false);
        setRollover(true);

        add( Box.createHorizontalGlue() );
        add( dateLabel );
        add( new JToolBar.Separator() );
        add( valueLabel );

        addContainerListener(new ContainerAdapter() {
            @Override
            public void componentRemoved(ContainerEvent arg0) {
                dispose();
            }
        });
    }

    private void dispose() {
        graph.removeGraphObserver(this);
    }

    @Override
    public void pointedDateChanged(Date date, Double value) {
        String dateString = ( date != null ? dateFormat.format( date ) : null );
        dateLabel.setText( dateString );
        valueLabel.setText( value != null ? Double.toString(trunc(value)) + Temperature.getUnitString() : null );
    }

    private double trunc(double x) {
        double invBase = 10;
        if ( x > 0 )
            return Math.floor(x * invBase)/invBase;
        else
            return Math.ceil(x * invBase)/invBase;
    }
}
