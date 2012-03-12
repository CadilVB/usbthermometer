/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Graph;

import Engine.Database;
import Main.Main;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author pawelkn
 */
public class Set {

    private Sensor sensor;
    private Color color;
    private Grid grid;
    private final List<Sample> samples = Collections.synchronizedList(new ArrayList<Sample>());
    private Stroke boldStroke = new BasicStroke(3);
    private Stroke basicStroke = new BasicStroke();

    protected Set(Sensor sensor, Color color, Grid grid) {
        this.sensor = sensor;
        this.color = color;
        this.grid = grid;
    }

    protected Color getColor() {
        return color;
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    protected void paint(Graphics g) {
        Sample lastSample = null;

        Calendar c = Calendar.getInstance();

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(color);
        g2d.setStroke(new BasicStroke());

        long visibleDistance = grid.getVisibleRightDate().getTime() - grid.getVisibleLeftDate().getTime();
        visibleDistance /= Toolkit.getDefaultToolkit().getScreenSize().width;
        visibleDistance *= 10;
        if (visibleDistance < 60000) {
            visibleDistance = 60000;
        }

        synchronized (samples) {
            boolean showDataPoints = true;

            if( samples.size() >= 3  ) {
                for(int i = 0; i < samples.size() - 1; i++) {
                    int first = grid.getHorizontalPosition(samples.get(i).getDateTimeCreation());
                    int second = grid.getHorizontalPosition(samples.get(i+1).getDateTimeCreation());
                    if( ( Math.abs(first - second) < 6 ) ) {
                        showDataPoints = false;
                        break;
                    }
                }
            }
            
            for (Sample sample : samples) {
                int x = grid.getHorizontalPosition(sample.getDateTimeCreation());
                int y = grid.getVeriticalPosition(sample.getValue());

                if (!showDataPoints) {
                    g2d.setStroke(boldStroke);
                } else {
                    g2d.setStroke(basicStroke);
                    g.fillRect(x - 1, y - 1, 3, 3);
                }

                if (lastSample != null) {
                    int lastX = grid.getHorizontalPosition(lastSample.getDateTimeCreation());
                    int lastY = grid.getVeriticalPosition(lastSample.getValue());

                    if ((lastX != 0) && (lastX <= x)) {

                        if (sample.getDateTimeCreation().getTime() - lastSample.getDateTimeCreation().getTime() < visibleDistance) {
                            g.drawLine(lastX, lastY, x, y);
                        }
                    }
                }

                lastSample = sample;
            }
        }
    }

    protected Sensor getSensor() {
        return sensor;
    }

    protected void update() {
        long timespan = (grid.getVisibleRightDate().getTime() - grid.getVisibleLeftDate().getTime()) / ( Toolkit.getDefaultToolkit().getScreenSize().width );
        List<Sample> temporaryList = Main.getLocalhost().getDatabase()
                .getSamples(sensor, grid.getVisibleLeftDate(), grid.getVisibleRightDate(), timespan, Integer.MAX_VALUE, Database.ASC );
        synchronized (samples) {
            samples.clear();
            samples.addAll(temporaryList);
        }
    }

    protected Sample getNearestSample(Date date) {
        synchronized (samples) {
            Sample result = null;
            if( samples.size() > 0 ) {
                long time = date.getTime();
                long maxDifference = ( grid.getVisibleRightDate().getTime() - grid.getVisibleLeftDate().getTime() ) /
                                    ( Toolkit.getDefaultToolkit().getScreenSize().width );
                long lastDifference = Long.MAX_VALUE;
                Sample nearestSample = null;
                for(Sample sample : samples) {
                    long newDifference = Math.abs(sample.getDateTimeCreation().getTime() - time);
                    if( lastDifference > newDifference ) {
                        nearestSample = sample;
                        lastDifference = newDifference;                    
                    }
                }
                if( lastDifference < maxDifference ) {
                    result = nearestSample;
                }
            }
            return result;
        }
    }

    protected void addSample(Sample sample) {
        if (sample.getSensor() == sensor) {
            synchronized (samples) {
                if (samples.size() > 0) {
                    Sample lastSample = samples.get(samples.size() - 1);

                    long visibleTimespan = grid.getVisibleRightDate().getTime() - grid.getVisibleLeftDate().getTime();
                    long updateTimespan = sample.getDateTimeCreation().getTime() - lastSample.getDateTimeCreation().getTime();
                    long dateToDelete = grid.getVisibleLeftDate().getTime();

                    if ( updateTimespan > visibleTimespan / ( Toolkit.getDefaultToolkit().getScreenSize().width ) ) {
                        List<Sample> samplesToDelete = new ArrayList<>();
                        for (Sample s : samples) {
                            if (s.getDateTimeCreation().getTime() < dateToDelete) {
                                samplesToDelete.add(s);
                            } else {
                                break;
                            }
                        }
                        samples.removeAll(samplesToDelete);
                        samples.add(sample);
                    }
                } else {
                    samples.add(sample);
                }
            }
        }
    }

    protected double getMinVisibleValue() {
        double minValue = Double.POSITIVE_INFINITY;

        synchronized (samples) {
            for (Sample sample : samples) {
                if ((sample.getDateTimeCreation().getTime() < grid.getVisibleRightDate().getTime()) &&
                        (sample.getDateTimeCreation().getTime() > grid.getVisibleLeftDate().getTime()) &&
                        (sample.getValue() < minValue)) {
                    minValue = sample.getValue();
                }
            }
        }

        return minValue;
    }

    protected double getMaxVisibleValue() {
        double maxValue = Double.NEGATIVE_INFINITY;

        synchronized (samples) {
            for (Sample sample : samples) {
                if ((sample.getDateTimeCreation().getTime() < grid.getVisibleRightDate().getTime()) &&
                        (sample.getDateTimeCreation().getTime() > grid.getVisibleLeftDate().getTime()) &&
                        (sample.getValue() > maxValue)) {
                    maxValue = sample.getValue();
                }
            }
        }

        return maxValue;
    }
}
