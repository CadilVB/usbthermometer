/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Tools;

import java.awt.Color;
import javax.swing.JTextField;

/**
 *
 * @author Paweł
 */
public class ToolBox  {

    public static void tryParseDouble(JTextField jtf) {
        try {
            Double.parseDouble(jtf.getText());
            jtf.setForeground(Color.BLACK);
        } catch (Exception e) {
            jtf.setForeground(Color.RED);
        }
    }

    public static void tryParseDoublePositive(JTextField jtf) {
        try {
            double number = Double.parseDouble(jtf.getText());
            if( number > 0 ) {
                jtf.setForeground(Color.BLACK);
            } else {
                jtf.setForeground(Color.RED);
            }
        } catch (Exception e) {
            jtf.setForeground(Color.RED);
        }
    }

    public static void tryParseInt(JTextField jtf) {
        try {
            Integer.parseInt(jtf.getText());
            jtf.setForeground(Color.BLACK);
        } catch (Exception e) {
            jtf.setForeground(Color.RED);
        }
    }

    public static void tryParseIntPositive(JTextField jtf) {
        try {
            int i = Integer.parseInt(jtf.getText());
            if( i > 0 ) {
                jtf.setForeground(Color.BLACK);
            } else {
                jtf.setForeground(Color.RED);
            }
        } catch (Exception e) {
            jtf.setForeground(Color.RED);
        }
    }
}
