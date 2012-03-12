/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Graph;

import USBThermometerLib.Temperature;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingUtilities;

/**
 *
 * @author pawelkn
 */
public class Grid {
    private Graph graph;

    private double lowVertical;
    private double highVertical;
    private double base;
    private double preferedVerticalGap;
    private int preferedVerticalGapsCount;

    private long leftTime;
    private long rightTime;

    private Rectangle workspaceBounds;

    private Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    
    private Font font;

    private static Color background;
    private static Color foreground;

    protected Grid(Graph graph, int w, int h) {
        this.graph = graph;
        this.font = graph.getFont();

        int valueWidth = SwingUtilities.computeStringWidth(graph.getFontMetrics(font), Double.toString(highVertical) + Temperature.getUnitString()) + 10;

        workspaceBounds = new Rectangle();
        workspaceBounds.x = valueWidth + 5;
        workspaceBounds.y = 15;
        workspaceBounds.width = w - 30 - workspaceBounds.x;
        workspaceBounds.height = h - 55 - workspaceBounds.y;
    }

    protected Grid(Graph graph) {
        this.graph = graph;
        this.font = graph.getFont();

        int valueWidth = SwingUtilities.computeStringWidth(graph.getFontMetrics(font), Double.toString(highVertical) + Temperature.getUnitString()) + 10;

        if( background == null ) {
            background = Color.WHITE;
        }
        if( foreground == null ) {
            foreground = Color.GRAY;
        }

        workspaceBounds = new Rectangle();
        workspaceBounds.x = valueWidth + 5;
        workspaceBounds.y = 15;
        workspaceBounds.width = graph.getGraphPanel().getPreferredSize().width - 30 - workspaceBounds.x;
        workspaceBounds.height = graph.getGraphPanel().getPreferredSize().height - 55 - workspaceBounds.y;
    }

    public static void setBackground(Color background) {
        Grid.background = background;
    }

    public static void setForeground(Color foreground) {
        Grid.foreground = foreground;
    }

    protected void paintGrid(int w, int h, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(font);
        g2d.setStroke(new BasicStroke());
        g2d.setColor(background);
        g2d.fillRect(0,0,w,h);
        g2d.setColor(foreground);

        int valueWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), Double.toString(highVertical) + Temperature.getUnitString()) + 10;

        workspaceBounds.x = valueWidth + 5;
        workspaceBounds.y = 15;
        workspaceBounds.width = w - 30 - workspaceBounds.x;
        workspaceBounds.height = h - 55 - workspaceBounds.y;

        g2d.setStroke(drawingStroke);
        if( workspaceBounds.height > preferedVerticalGapsCount * 12 ) {
            for( int i = 1; i < preferedVerticalGapsCount; i++ ) {
                int position = (int)((double)(h - 50 - 20)/(double)(preferedVerticalGapsCount) * i + 20);
                g2d.drawLine(valueWidth, position - 5 , w-30, position - 5 );
            }
        } else {
            int position = (int)((double)(h - 50 - 20)/2.0 + 20);
            g2d.drawLine(valueWidth, position - 5 , w-30, position - 5 );
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format( new Date(leftTime) );
        int dateWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), dateString);

        g2d.setStroke(drawingStroke);

        int dateGapsCount = (int)( (double)( w - 30 - ( valueWidth + 5 ) ) / ( (double)dateWidth * 1.5 ) );
        double dateGapLength = (double)( w - 30 - ( valueWidth + 5 ) ) / (double)dateGapsCount;
        for( int i = 1; i <= dateGapsCount; i++ ) {
            g2d.drawLine((int)(valueWidth + 5 + dateGapLength * i), 15, (int)(valueWidth + 5 + dateGapLength * i), h-50);
        }
    }

    protected void paintBorder(int w, int h, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(font);
        g2d.setStroke(new BasicStroke());
        g2d.setColor(foreground);

        int valueWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), Double.toString(highVertical) + Temperature.getUnitString()) + 10;

        workspaceBounds.x = valueWidth + 5;
        workspaceBounds.y = 15;
        workspaceBounds.width = w - 30 - workspaceBounds.x;
        workspaceBounds.height = h - 55 - workspaceBounds.y;

        g2d.setColor(background);
        g2d.fillRect(0, 0, workspaceBounds.x, h);
        g2d.fillRect(0, 0, w, workspaceBounds.y );
        g2d.fillRect(0, h - 55, w, 55);
        g2d.fillRect(w - 30, 0, 30, h);

        g2d.setColor(foreground);
        g2d.setStroke(drawingStroke);

        if( workspaceBounds.height > preferedVerticalGapsCount * 12 ) {
            for( int i = 0; i < preferedVerticalGapsCount + 1; i++ ) {
                int position = (int)((double)(h - 50 - 20)/(double)(preferedVerticalGapsCount) * i + 20);
                g2d.drawString(Double.toString(trunc(lowVertical + preferedVerticalGap * (preferedVerticalGapsCount - i), base)) + Temperature.getUnitString(), 3, position);
            }
        } else {
            int position = (int)((double)(h - 50 - 20)/2.0 + 20);
            g2d.drawString(Double.toString(trunc(highVertical, base)) + Temperature.getUnitString(), 3, 20);
            g2d.drawString(Double.toString(trunc(lowVertical + preferedVerticalGap * ((double)preferedVerticalGapsCount/2.0), base)) + Temperature.getUnitString(), 3, position);
            g2d.drawString(Double.toString(trunc(lowVertical, base)) + Temperature.getUnitString(), 3, h-50);
        }

        g2d.setStroke(new BasicStroke());
        g2d.drawLine(valueWidth,h-55,w-30,h-55);
        g2d.drawLine(valueWidth + 5,15,valueWidth + 5,h-50);
        g2d.drawLine(valueWidth,15,w-30,15);
        g2d.drawLine(w-30,15,w-30,h-50);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = dateFormat.format( new Date(leftTime) );
        String timeString = timeFormat.format( new Date(leftTime) );
        int dateWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), dateString);
        int timeWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), timeString);

        g2d.drawString( dateString, valueWidth + 5 - dateWidth/2, h-35);
        g2d.drawString( timeString, valueWidth + 5 - timeWidth/2, h-23);

        int dateGapsCount = (int)( (double)( w - 30 - ( valueWidth + 5 ) ) / ( (double)dateWidth * 1.5 ) );
        double dateGapLength = (double)( w - 30 - ( valueWidth + 5 ) ) / (double)dateGapsCount;
        for( int i = 1; i <= dateGapsCount; i++ ) {
            long timespan = leftTime + (rightTime - leftTime) * i / dateGapsCount;
            dateString = dateFormat.format( new Date(timespan) );
            timeString = timeFormat.format( new Date(timespan) );
            g2d.drawString( dateString, (int)(valueWidth + 5 + dateGapLength * i - dateWidth/2), h-35);
            g2d.drawString( timeString, (int)(valueWidth + 5 + dateGapLength * i - timeWidth/2), h-23);
        }
    }

    public double getVisibleLowValue() {
        return lowVertical;
    }

    public double getVisibleHighValue() {
        return highVertical;
    }

    protected Rectangle getWorkspaceBounds() {
        return workspaceBounds;
    }

    public Date getVisibleLeftDate() {
        return new Date(leftTime);
    }

    public Date getVisibleRightDate() {
        return new Date(rightTime);
    }

    /**
     * @param lowX the lowX to set
     */
    protected void setValueRange(double lowY, double highY) {
        double difference = highY - lowY;
        difference = trunc(difference, 0.1);
        countBase(difference);        
        this.lowVertical = trunc(lowY - Math.abs(lowY % (base)), base);

        double correctedDifference = difference % ( base * 10 );
        if( correctedDifference > ( base * 10 ) / 2 ) {
            preferedVerticalGap = trunc(( difference - correctedDifference ) / 10 + base, base);
        } else  {
            preferedVerticalGap = trunc(( difference - correctedDifference ) / 10, base);
        }
        if( preferedVerticalGap < 0.1 ) {
            preferedVerticalGap = 0.1;
        }
        
        double newHighY = this.lowVertical;
        int newGapCount = 0;
        while( newHighY < highY ) {
            newHighY += preferedVerticalGap;
            newGapCount++;
        }
        
        this.highVertical = roundOneDecimal(newHighY);
        preferedVerticalGapsCount = newGapCount;
    }

    /**
     * @param leftDate the leftDate to set
     */
    protected void setDateRange(Date leftDate, Date rightDate) {
        leftTime = leftDate.getTime();
        rightTime = (rightDate.getTime() > new Date().getTime() ? new Date().getTime() : rightDate.getTime());
    }

    protected int getVeriticalPosition(double val) {
        double a = (double)(workspaceBounds.height) / (double)(lowVertical - highVertical);
        double b = (double)(workspaceBounds.y) - a *(double)(highVertical);
        return (int)(a * val + b);
    }

    protected int getHorizontalPosition(Date date) {
        double a = (double)(workspaceBounds.width) / (double)(rightTime - leftTime);
        double b = (double)(workspaceBounds.x) - a *(double)(leftTime);
        return (int)(a * date.getTime() + b);
    }

    protected double getVerticalValue(int position) {
        double a = (double)(lowVertical - highVertical) / (double)(workspaceBounds.height);
        double b = (double)(highVertical) - a * (double)(workspaceBounds.y);
        return (a * position + b);
    }
    
    protected Date getHorizontalDate(int position) {
        double a = (double)(rightTime - leftTime) / (double)(workspaceBounds.width);
        double b = (double)(leftTime) - a * (double)(workspaceBounds.x);
        return new Date((long)(a * position + b));
    }

    private void countBase(double val) {
        double tmp = 1;
        if( val != 0.0 ) {
            while(true) {
                if( (val < tmp * 10 )&&(val >= tmp) ) {
                    break;
                } else if( val >= tmp * 10 ) {
                    tmp *= 10;
                } else {
                    tmp /= 10;
                }
            }
        }
        if( tmp < 1 ) {
            tmp = 1;
        } 
        this.base = tmp / 10;
    }

    private double trunc(double x, double base) {
        double invBase = 1 / base;
        if ( x > 0 ) {
            x += base / 10;
            return Math.floor(x * invBase)/invBase;
        } else {
            x -= base / 10;
            return Math.ceil(x * invBase)/invBase;
        }
    }

    private double roundOneDecimal(double d) {
        double result = d * 10;
        result = Math.round(result);
        result = result / 10;
        return result;
    }
}
