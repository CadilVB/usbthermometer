/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Graph;

import Engine.Host;
import Engine.HostObserver;
import Main.Main;
import Tools.CharsetControl;
import Tools.ConfigurationManager;
import USBThermometerLib.Device;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import Visual.Tools.GraphObserver;
import Visual.Tools.InternalFrameObservable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author pawelkn
 */
public class Graph extends JPanel implements HostObserver {

    private final List<Set> sets = new ArrayList<Set>();
    private final List<Sensor> sensors = new ArrayList<>();
    private Grid grid;
    private Rectangle zoomRectangle;
    private boolean visibleZoomRectangle;
    private Point zoomingStart;
    private static final Stroke zoomStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
    private boolean autoShiftLeft = true;
    private static List<Color> colorList;
    private boolean zoomMode = false;
    private static final Image magnifierCursor = new ImageIcon("graphics/magnifier-left.png").getImage();
    private static ResourceBundle bundle;
    private final Point hotspot = new Point(6, 6);
    private final String name = "My Cursor";
    private JComponent parent;
    private boolean guideMode = false;
    private Set guideSet;
    private Point guidePoint;
    private final List<GraphObserver> gos = new ArrayList<>();
    private BufferedImage image;

    public Graph(BufferedImage im) {
        this.image = im;
        grid = new Grid(this, im.getWidth(), im.getHeight());
        createGraph();
    }

    protected Graph(JComponent parent) {
        this.parent = parent;
        grid = new Grid(this);
        createGraph();
    }

    private void createGraph() {

        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        if (colorList == null) {
            colorList = Collections.synchronizedList(new ArrayList<Color>());
            colorList.add(new Color(0x0099cc));
            colorList.add(Color.GREEN);
            colorList.add(Color.RED);
            colorList.add(Color.MAGENTA);
            colorList.add(Color.ORANGE);
            colorList.add(Color.PINK);
            colorList.add(Color.YELLOW);
            colorList.add(Color.BLUE);
            colorList.add(Color.LIGHT_GRAY);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, -3);
        grid.setValueRange(-55, 128);
        grid.setDateRange(c.getTime(), new Date());

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent evt) {
                graphMouseReleased(evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                graphMousePressed(evt);
            }

            @Override
            public void mouseClicked(MouseEvent evt) {
                graphMouseClicked(evt);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                graphMouseExited(evt);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent evt) {
                graphMouseDragged(evt);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                graphMouseMoved(evt);
            }
        });
    }

    protected JComponent getGraphPanel() {
        return parent;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public static void setColorList(List<Color> colorList) {
        Graph.colorList = colorList;
    }

    public boolean addSensor(Sensor sensor) {
            for (Set set : sets) {
                if (set.getSensor() == sensor) {
                    return false;
                }
            }

        Color setColor = Color.RED;
        for (Color color : colorList) {
            boolean colorNotUsed = true;
                for (Set set : sets) {
                    if (set.getColor().equals(color)) {
                        colorNotUsed = false;
                        break;
                    }
                }
            if (colorNotUsed) {
                setColor = color;
                break;
            }
        }

        Set set = new Set(sensor, setColor, grid);
            sets.add(set);
        sensors.add(sensor);

        List<Set> setList = new ArrayList<>();
        setList.add(set);
        updateSets(setList);

        updateFrameTitle();
        autorange();
        repaint();

        Main.getLocalhost().addHostObserver(this);

        return true;
    }

    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);

        List<Set> setsCopy = new ArrayList<>();
            setsCopy.addAll(sets);

        for (Set set : setsCopy) {
            if (set.getSensor() == sensor) {
                    sets.remove(set);
                break;
            }
        }

        if (sets.isEmpty()) {
            Main.getLocalhost().removeHostObserver(this);
        } else {
            update();
            autorange();
        }

        updateFrameTitle();
        repaint();
    }

    public Color getSensorColor(Sensor sensor) {
            for (Set set : sets) {
                if (set.getSensor() == sensor) {
                    return set.getColor();
                }
            }
        return null;
    }

    public void setSensorColor(Sensor sensor, Color color) {
            for (Set set : sets) {
                if (set.getSensor() == sensor) {
                    set.setColor(color);
                    return;
                }
            }
    }

    protected void zoom(int x1, int y1, int x2, int y2) {
        grid.setDateRange(grid.getHorizontalDate(x1), grid.getHorizontalDate(x2));
        grid.setValueRange(grid.getVerticalValue(y2), grid.getVerticalValue(y1));

        if (grid.getHorizontalDate(Math.max(x2, x1)).getTime() < new Date().getTime()) {
            autoShiftLeft = false;
        } else {
            autoShiftLeft = true;
        }

        updateSets(sets);
    }

    public void setAutoShiftLeft(boolean autoShiftLeft) {
        this.autoShiftLeft = autoShiftLeft;
    }

    public boolean getAutoShiftLeft() {
        return autoShiftLeft;
    }

    public Grid getGrid() {
        return grid;
    }

    public void autorange() {
        double minValue = Double.POSITIVE_INFINITY;
        double maxValue = Double.NEGATIVE_INFINITY;

            for (Set set : sets) {
                if (set.getMinVisibleValue() < minValue) {
                    minValue = set.getMinVisibleValue();
                }
                if (set.getMaxVisibleValue() > maxValue) {
                    maxValue = set.getMaxVisibleValue();
                }
            }

        if ((minValue != Double.POSITIVE_INFINITY) && (maxValue != Double.NEGATIVE_INFINITY)) {
            grid.setValueRange(minValue - 0.05, maxValue + 0.1);
        }
    }

    @Override
    public void paint(Graphics g) {
        paint(g, getWidth(), getHeight());
    }

    public void paint() {
        paint(image.getGraphics(), image.getWidth(), image.getHeight());
    }

    private void paint(Graphics g, int w, int h) {
        Graphics2D g2d = (Graphics2D) g;

        grid.paintGrid(w, h, g);

            for (Set set : sets) {
                set.paint(g);
            }

        if (guideMode && (guidePoint != null)) {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(zoomStroke);
            g2d.drawLine(guidePoint.x, 0, guidePoint.x, h);
            g2d.drawLine(0, guidePoint.y, w, guidePoint.y);
        }

        grid.paintBorder(w, h, g);

        if (visibleZoomRectangle) {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(zoomStroke);
            g2d.drawRect(zoomRectangle.x, zoomRectangle.y, zoomRectangle.width, zoomRectangle.height);
        }

        int textGap = w / (sets.size() + 1);
        int textPos = textGap;
            for (Set set : sets) {
                String text = set.getSensor().getName();
                int textWidth = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), text);
                g2d.setColor(set.getColor());
                g2d.drawString(text, textPos - textWidth / 2, h - 3);
                textPos += textGap;
            }
    }

    public void setDateRange(Date leftDate, Date rightDate) {
        grid.setDateRange(leftDate, rightDate);
        updateSets(sets);
    }

    public void setValueRange(double lowY, double highY) {
        grid.setValueRange(lowY, highY);
    }

    private void updateSets(List<Set> sets) {
        Cursor previousCursor = null;
        Cursor parentPreviousCursor = null;

        if (parent != null) {
            previousCursor = getCursor();
            parentPreviousCursor = parent.getCursor();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

            for (Set set : sets) {
                set.update();
            }

        if (parent != null) {
            this.setCursor(previousCursor);
            parent.setCursor(parentPreviousCursor);
        }
    }

    public void update() {
        updateSets(sets);
    }

    public void setZoomMode(boolean state) {
        zoomMode = state;

        if (zoomMode) {
            setCursor(getToolkit().createCustomCursor(magnifierCursor, hotspot, name));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    protected void setGuideMode(boolean guideMode, Sensor guidedSensor) {
        this.guideMode = guideMode;
            for (Set set : sets) {
                if (set.getSensor() == guidedSensor) {
                    guideSet = set;
                    break;
                }
            }

        if (guideMode) {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    protected void addGraphObserver(GraphObserver go) {
        gos.add(go);
    }

    protected void removeGraphObserver(GraphObserver go) {
        gos.remove(go);
    }

    private void notifyPointedDateChanged(Date date, Double value) {
        for (GraphObserver go : gos) {
            go.pointedDateChanged(date, value);
        }
    }

    private void graphMouseExited(MouseEvent e) {
        notifyPointedDateChanged(null, null);
    }

    private void graphMouseMoved(MouseEvent evt) {
        if (guideMode) {
            Date date = grid.getHorizontalDate(evt.getX());
            Sample sample = guideSet.getNearestSample(date);

            Point p = null;
            if (sample != null) {
                p = new Point();
                p.y = grid.getVeriticalPosition(sample.getValue());
                p.x = grid.getHorizontalPosition(sample.getDateTimeCreation());
                notifyPointedDateChanged(sample.getDateTimeCreation(), sample.getValue());
            } else {
                notifyPointedDateChanged(null, null);
            }

            if (((p == null) && (guidePoint != null)) || ((p != null) && (!p.equals(guidePoint)))) {
                guidePoint = p;
                repaint();
            }
        } else {
            notifyPointedDateChanged(null, null);
        }
    }

    private void graphMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            setZoomMode(false);
            setGuideMode(false, null);
            repaint();
        }
    }

    private void graphMousePressed(MouseEvent evt) {
        if (zoomMode) {
            zoomingStart = evt.getPoint();
            zoomRectangle = new Rectangle();
            visibleZoomRectangle = true;
        }
    }

    private void graphMouseReleased(MouseEvent evt) {
        if (zoomMode) {
            visibleZoomRectangle = false;
            if ((zoomingStart.x != evt.getPoint().x) && (zoomingStart.y != evt.getPoint().y)) {
                zoom(Math.min(zoomingStart.x, evt.getPoint().x),
                        Math.min(zoomingStart.y, evt.getPoint().y),
                        Math.max(zoomingStart.x, evt.getPoint().x),
                        Math.max(zoomingStart.y, evt.getPoint().y));
                repaint();
            }
        }
    }

    private void graphMouseDragged(MouseEvent evt) {
        if (visibleZoomRectangle) {
            Rectangle rect = new Rectangle(Math.min(zoomingStart.x, evt.getPoint().x),
                    Math.min(zoomingStart.y, evt.getPoint().y),
                    Math.abs(evt.getPoint().x - zoomingStart.x),
                    Math.abs(evt.getPoint().y - zoomingStart.y));
            zoomRectangle = rect;
            repaint();
        }
    }

    protected void updateFrameTitle() {
        if ((parent != null) && (parent instanceof GraphPanel)) {
            InternalFrameObservable ifo = ((InternalFrameObservable) ((GraphPanel) parent).getInternalFrame());
            if (sensors.isEmpty()) {
                ifo.setTitle(bundle.getString("GRAPH"));
                ifo.notifyTitleChanged(bundle.getString("GRAPH"));
            } else if (sensors.size() == 1) {
                ifo.setTitle(bundle.getString("GRAPH_-_") + sensors.get(0).getName());
                ifo.notifyTitleChanged(bundle.getString("GRAPH_-_") + sensors.get(0).getName());
            } else {
                ifo.setTitle(bundle.getString("GRAPH_-_") + sensors.get(0).getName() + ", ...");
                ifo.notifyTitleChanged(bundle.getString("GRAPH_-_") + sensors.get(0).getName() + ", ...");
            }
        }
    }

    @Override
    public void notiffyNewSample(Host host, Sample sample) {
        if (autoShiftLeft) {
            if (sensors.contains(sample.getSensor())) {
                long updateTimespan = sample.getDateTimeCreation().getTime() - grid.getVisibleRightDate().getTime();
                long newLeftTimespan = grid.getVisibleLeftDate().getTime() + updateTimespan;
                long newRightTimespan = grid.getVisibleRightDate().getTime() + updateTimespan;
                grid.setDateRange(new Date(newLeftTimespan), new Date(newRightTimespan));

                    for (Set set : sets) {
                        set.addSample(sample);
                    }
                autorange();
                repaint();
            }
        }
    }

    @Override
    public void notiffySensorUpdated(Host host, Sensor sensor) {
        for (Sensor s : sensors) {
            if (s == sensor) {
                updateFrameTitle();
                repaint();
                return;
            }
        }
    }

    @Override
    public void notiffyDeviceUpdated(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceAdd(Host host, Device device) {
    }

    @Override
    public void notiffyDeviceRemoved(Host host, Device device) {
    }
}