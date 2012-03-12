/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author pawelkn
 */
public class TemperatureBarChart extends JPanel{

    private static final int max = 135;
    private static final int min = -55;
    private int value = -55;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaximum() {
        return max;
    }

    public int getMinimum() {
        return min;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        int w = getWidth();
        int h = getHeight();

        if( h > 5 ) {       
            g2d.setColor(Color.black);
            g2d.drawRoundRect(5, 0, w-6, h-1, 2, 2);

            double a = (double)(min - max) / (double)(h - 3);
            double b = max - a;

            int m1 = h / 3;
            int m2 = 2 * h / 3;

            g2d.setColor(Color.red);

            float red = 0;
            float green = 0;
            float blue = 0;
            float value = 0;

            int valueX = (int)((getValue() - b)/a);

            for (int x = 1; x < h - 1; x++) {
                if (x < m1) {
                    value = 1.0f / (float) (m1) * x;
                    red = 1.0f;
                    green = value;
                    blue = 0.0f;
                } else if (x < m2) {
                    value = 1.0f / (float) (m2 - m1) * x - (float) m1 / (float) (m2 - m1);
                    red = 1.0f;
                    green = 1.0f;
                    blue = value;
                } else {
                    value = 1.0f / (float) (h - m2) * x - (float) m2 / (float) (h - m2);
                    red = 1.0f - value;
                    green = 1.0f - value;
                    blue = 1.0f;
                }

                if (x == valueX) {
                    g2d.setColor( Color.black );
                    g2d.drawLine(5, valueX, w - 2, valueX);
                } else {
                    for (int j = 5; j < w - 1; j++) {
                        g2d.setColor(new Color(red, green, blue, ((float) j * 0.02f > 1.0f ? 1.0f : (float) j * 0.02f)));
                        g2d.fillRect(j, x, 1, 1);
                    }
                }
            }

            g2d.setColor(Color.darkGray);

            if( h > 90 ) {
                for (double i = (-50.0 - b) / a; i > 0; i += (10.0 / a)) {
                    g2d.drawLine(0, (int) i, 5, (int) i);
                }

                for (double i = (-55.0 - b) / a; i > 0; i += (10.0 / a)) {
                    g2d.drawLine(4, (int) i, 5, (int) i);
                }

                int val = -40;
                for (double i = (-40.0 - b) / a; i > 0; i += (20.0 / a)) {
                    g2d.drawString(Integer.toString(val), 8, (int) i + 4);
                    val += 20;
                }
            } else if( h > 60 ) {
                for (double i = (-40.0 - b) / a; i > 0; i += (20.0 / a)) {
                    g2d.drawLine(0, (int) i, 5, (int) i);
                }

                for (double i = (-50.0 - b) / a; i > 0; i += (20.0 / a)) {
                    g2d.drawLine(4, (int) i, 5, (int) i);
                }

                int val = -40;
                for (double i = (-40.0 - b) / a; i > 0; i += (40.0 / a)) {
                    g2d.drawString(Integer.toString(val), 8, (int) i + 4);
                    val += 40;
                }
            }
        }
    }
}
