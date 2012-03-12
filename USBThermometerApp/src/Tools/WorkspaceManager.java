/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Main.Main;
import USBThermometerLib.Sensor;
import Visual.BarPanel;
import Visual.Graph.Graph;
import Visual.Graph.GraphPanel;
import Visual.Graph.Grid;
import Visual.MainForm;
import Visual.TablePanel;
import Visual.TableSettingsDialog;
import Visual.Tools.InternalFrameAllwaysOnTop;
import Visual.Tools.InternalFrameObservable;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Paweł
 */
public class WorkspaceManager {
    private static ResourceBundle bundle;

    public static void save(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("usbthermometer-workspace");
            doc.appendChild(rootElement);

            MainForm mf = MainForm.getInstance();

            Element xPosMainEle = doc.createElement("x-pos");
            xPosMainEle.appendChild(doc.createTextNode(Integer.toString(mf.getLocation().x)));
            rootElement.appendChild(xPosMainEle);

            Element yPosMainEle = doc.createElement("y-pos");
            yPosMainEle.appendChild(doc.createTextNode(Integer.toString(mf.getLocation().y)));
            rootElement.appendChild(yPosMainEle);

            Element widthMainEle = doc.createElement("width");
            widthMainEle.appendChild(doc.createTextNode(Integer.toString(mf.getWidth())));
            rootElement.appendChild(widthMainEle);

            Element heightMainEle = doc.createElement("height");
            heightMainEle.appendChild(doc.createTextNode(Integer.toString(mf.getHeight())));
            rootElement.appendChild(heightMainEle);

            List visualElements = mf.getVisualElemets();
            for (Object obj : visualElements) {
                if (obj instanceof JInternalFrame) {
                    Element iFrameEle = doc.createElement("internal-frame");
                    rootElement.appendChild(iFrameEle);

                    InternalFrameObservable iFrame = (InternalFrameObservable) obj;

                    Element xPosEle = doc.createElement("x-pos");
                    xPosEle.appendChild(doc.createTextNode(Integer.toString(iFrame.getLocation().x)));
                    iFrameEle.appendChild(xPosEle);

                    Element yPosEle = doc.createElement("y-pos");
                    yPosEle.appendChild(doc.createTextNode(Integer.toString(iFrame.getLocation().y)));
                    iFrameEle.appendChild(yPosEle);

                    Element widthEle = doc.createElement("width");
                    widthEle.appendChild(doc.createTextNode(Integer.toString(iFrame.getWidth())));
                    iFrameEle.appendChild(widthEle);

                    Element heightEle = doc.createElement("height");
                    heightEle.appendChild(doc.createTextNode(Integer.toString(iFrame.getHeight())));
                    iFrameEle.appendChild(heightEle);

                    for (Component c : iFrame.getContentPane().getComponents()) {
                        if (c instanceof BarPanel) {
                            Element barPanelEle = doc.createElement("bar-panel");
                            iFrameEle.appendChild(barPanelEle);

                            BarPanel barPanel = (BarPanel) c;

                            if (barPanel.getSensor() != null) {
                                Element sensorEle = doc.createElement("sensor");
                                sensorEle.appendChild(doc.createTextNode(barPanel.getSensor().getStringId()));
                                barPanelEle.appendChild(sensorEle);
                            }
                        } else if (c instanceof GraphPanel) {
                            Element graphPanelEle = doc.createElement("graph-panel");
                            iFrameEle.appendChild(graphPanelEle);

                            GraphPanel graphPanel = (GraphPanel) c;
                            Graph graph = graphPanel.getGraph();

                            Element sensorsEle = doc.createElement("sensors");
                            graphPanelEle.appendChild(sensorsEle);

                            for (Sensor sensor : graph.getSensors()) {
                                Element sensorEle = doc.createElement("sensor");
                                sensorsEle.appendChild(sensorEle);

                                Element sensorIdEle = doc.createElement("id");
                                sensorEle.appendChild(sensorIdEle);
                                sensorIdEle.appendChild(doc.createTextNode(sensor.getStringId()));

                                Element sensorColorEle = doc.createElement("color");
                                sensorEle.appendChild(sensorColorEle);
                                sensorColorEle.appendChild(doc.createTextNode(Integer.toString( graph.getSensorColor(sensor).getRGB() )));
                            }

                            Element autoShiftLeftEle = doc.createElement("auto-shift-left");
                            autoShiftLeftEle.appendChild(doc.createTextNode(Boolean.toString(graph.getAutoShiftLeft())));
                            graphPanelEle.appendChild(autoShiftLeftEle);

                            Grid grid = graph.getGrid();

                            Element leftDate = doc.createElement("left-date");
                            leftDate.appendChild(doc.createTextNode(Long.toString(grid.getVisibleLeftDate().getTime())));
                            graphPanelEle.appendChild(leftDate);

                            Element rightDate = doc.createElement("right-date");
                            rightDate.appendChild(doc.createTextNode(Long.toString(grid.getVisibleRightDate().getTime())));
                            graphPanelEle.appendChild(rightDate);

                            Element highValueDate = doc.createElement("high-value");
                            highValueDate.appendChild(doc.createTextNode(Double.toString(grid.getVisibleHighValue())));
                            graphPanelEle.appendChild(highValueDate);

                            Element lowValueEle = doc.createElement("low-value");
                            lowValueEle.appendChild(doc.createTextNode(Double.toString(grid.getVisibleLowValue())));
                            graphPanelEle.appendChild(lowValueEle);

                        } else if (c instanceof TablePanel) {
                            Element graphPanelEle = doc.createElement("table-panel");
                            iFrameEle.appendChild(graphPanelEle);

                            TablePanel tablePanel = (TablePanel) c;

                            Element sensorsEle = doc.createElement("sensors");
                            graphPanelEle.appendChild(sensorsEle);

                            for (Sensor sensor : tablePanel.getSensors()) {
                                Element sensorEle = doc.createElement("sensor");
                                sensorsEle.appendChild(sensorEle);

                                Element sensorIdEle = doc.createElement("id");
                                sensorEle.appendChild(sensorIdEle);
                                sensorIdEle.appendChild(doc.createTextNode(sensor.getStringId()));
                            }

                            TableSettingsDialog tsd = tablePanel.getTableSettingsDialog();

                            Element dateTimeTypeEle = doc.createElement("date-time-type");
                            dateTimeTypeEle.appendChild(doc.createTextNode(Integer.toString(tsd.getDateTimeType())));
                            graphPanelEle.appendChild(dateTimeTypeEle);

                            Element highRangeEle = doc.createElement("high-range");
                            highRangeEle.appendChild(doc.createTextNode(Double.toString(tsd.getHighRange())));
                            graphPanelEle.appendChild(highRangeEle);

                            Element lastSavedValueEle = doc.createElement("last-saved-value");
                            lastSavedValueEle.appendChild(doc.createTextNode(Integer.toString(tsd.getLastSavedValue())));
                            graphPanelEle.appendChild(lastSavedValueEle);

                            Element lastTimespanEle = doc.createElement("last-timespan");
                            lastTimespanEle.appendChild(doc.createTextNode(Integer.toString(tsd.getLastTimespan())));
                            graphPanelEle.appendChild(lastTimespanEle);

                            Element lastTimespanTypeEle = doc.createElement("last-timespan-type");
                            lastTimespanTypeEle.appendChild(doc.createTextNode(Integer.toString(tsd.getLastTimespanType())));
                            graphPanelEle.appendChild(lastTimespanTypeEle);

                            Element limitEle = doc.createElement("limit");
                            limitEle.appendChild(doc.createTextNode(Integer.toString(tsd.getLimit())));
                            graphPanelEle.appendChild(limitEle);

                            Element lowRangeEle = doc.createElement("low-range");
                            lowRangeEle.appendChild(doc.createTextNode(Double.toString(tsd.getLowRange())));
                            graphPanelEle.appendChild(lowRangeEle);

                            Element orderByEle = doc.createElement("order-by");
                            orderByEle.appendChild(doc.createTextNode(Integer.toString(tsd.getOrderBy())));
                            graphPanelEle.appendChild(orderByEle);

                            Element periodHighDateEle = doc.createElement("period-high-date");
                            periodHighDateEle.appendChild(doc.createTextNode(Long.toString(tsd.getPeriodHighDate().getTime())));
                            graphPanelEle.appendChild(periodHighDateEle);

                            Element periodLowDateEle = doc.createElement("period-low-date");
                            periodLowDateEle.appendChild(doc.createTextNode(Long.toString(tsd.getPeriodLowDate().getTime())));
                            graphPanelEle.appendChild(periodLowDateEle);
                        }
                    }
                } else if (obj instanceof JDialog) {
                    Element iFrameEle = doc.createElement("dialog");
                    rootElement.appendChild(iFrameEle);

                    JDialog dialog = (JDialog) obj;

                    Element xPosEle = doc.createElement("x-pos");
                    xPosEle.appendChild(doc.createTextNode(Integer.toString(dialog.getLocation().x)));
                    iFrameEle.appendChild(xPosEle);

                    Element yPosEle = doc.createElement("y-pos");
                    yPosEle.appendChild(doc.createTextNode(Integer.toString(dialog.getLocation().y)));
                    iFrameEle.appendChild(yPosEle);

                    Element widthEle = doc.createElement("width");
                    widthEle.appendChild(doc.createTextNode(Integer.toString(dialog.getWidth())));
                    iFrameEle.appendChild(widthEle);

                    Element heightEle = doc.createElement("height");
                    heightEle.appendChild(doc.createTextNode(Integer.toString(dialog.getHeight())));
                    iFrameEle.appendChild(heightEle);

                    for (Component c : dialog.getContentPane().getComponents()) {
                        if (c instanceof BarPanel) {
                            Element barPanelEle = doc.createElement("bar-panel");
                            iFrameEle.appendChild(barPanelEle);

                            BarPanel barPanel = (BarPanel) c;

                            if (barPanel.getSensor() != null) {
                                Element sensorEle = doc.createElement("sensor");
                                sensorEle.appendChild(doc.createTextNode(barPanel.getSensor().getStringId()));
                                barPanelEle.appendChild(sensorEle);
                            }
                        }
                    }
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));

            transformer.transform(source, result);
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    public static void save() {
        save("default.utw");
    }

    public static void load(String filename, final MainForm mainForm) {
        Locale locale = new Locale(ConfigurationManager.load().getLocale());
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        try {
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Element docElement = doc.getDocumentElement();
            final int xMf = Integer.parseInt(getTagValue("x-pos", docElement));
            final int yMf = Integer.parseInt(getTagValue("y-pos", docElement));
            final int widthMf = Integer.parseInt(getTagValue("width", docElement));
            final int heightMf = Integer.parseInt(getTagValue("height", docElement));

            NodeList dialogList = docElement.getElementsByTagName("dialog");

            for (int i = 0; i < dialogList.getLength(); i++) {
                Node dialogNode = dialogList.item(i);
                if (dialogNode.getNodeType() == Node.ELEMENT_NODE) {

                    final JDialog dialog = new JDialog();
                    Element dialogElement = (Element) dialogNode;

                    int x = Integer.parseInt(getTagValue("x-pos", dialogElement));
                    int y = Integer.parseInt(getTagValue("y-pos", dialogElement));
                    int width = Integer.parseInt(getTagValue("width", dialogElement));
                    int height = Integer.parseInt(getTagValue("height", dialogElement));
                    dialog.setBounds(x, y, width, height);
                    dialog.setIconImage(Toolkit.getDefaultToolkit().getImage("graphics/kni - ikona16.png"));
                    dialog.setAlwaysOnTop(true);

                    NodeList barPanelList = dialogElement.getElementsByTagName("bar-panel");
                    for (int j = 0; j < barPanelList.getLength(); j++) {
                        final Node barPanelNode = barPanelList.item(j);
                        if ((barPanelNode != null) && (barPanelNode.getNodeType() == Node.ELEMENT_NODE)) {

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {

                                    Element barPanelElement = (Element) barPanelNode;
                                    final BarPanel barPanel = new BarPanel(dialog);
                                    dialog.setTitle(bundle.getString("BAR_GRAPH"));
                                    dialog.setMaximumSize(new Dimension(BarPanel.MAX_WIDTH, Integer.MAX_VALUE));
                                    dialog.add(barPanel);
                                    dialog.addWindowListener(new WindowAdapter() {

                                        @Override
                                        public void windowClosing(WindowEvent e) {
                                            barPanel.close();
                                        }
                                    });

                                    String sensorId = getTagValue("sensor", barPanelElement);
                                    Sensor sensor = Main.findSensorByStringId(sensorId);
                                    barPanel.setSensor(sensor);

                                    mainForm.addVisualElement(dialog);
                                    dialog.setVisible(true);

                                }
                            });
                        }
                    }
                }
            }

            NodeList iFrameList = doc.getDocumentElement().getElementsByTagName("internal-frame");

            for (int i = 0; i < iFrameList.getLength(); i++) {
                Node iFrameNode = iFrameList.item(i);
                if (iFrameNode.getNodeType() == Node.ELEMENT_NODE) {

                    final Element iFrameElemet = (Element) iFrameNode;

                    final int x = Integer.parseInt(getTagValue("x-pos", iFrameElemet));
                    final int y = Integer.parseInt(getTagValue("y-pos", iFrameElemet));
                    final int width = Integer.parseInt(getTagValue("width", iFrameElemet));
                    final int height = Integer.parseInt(getTagValue("height", iFrameElemet));

                    final NodeList barPanelList = iFrameElemet.getElementsByTagName("bar-panel");

                    for (int j = 0; j < barPanelList.getLength(); j++) {
                        final Node barPanelNode = barPanelList.item(j);
                        if ((barPanelNode != null) && (barPanelNode.getNodeType() == Node.ELEMENT_NODE)) {

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {

                                    InternalFrameAllwaysOnTop topFrame = new InternalFrameAllwaysOnTop(null, true, true);
                                    topFrame.setBounds(x, y, width, height);
                                    topFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));

                                    Element barPanelElement = (Element) barPanelNode;
                                    final BarPanel barPanel = new BarPanel(topFrame);
                                    topFrame.setTitle(bundle.getString("BAR_GRAPH"));
                                    topFrame.setMaximumSize(new Dimension(BarPanel.MAX_WIDTH, Integer.MAX_VALUE));
                                    topFrame.add(barPanel);
                                    topFrame.addInternalFrameListener(new InternalFrameAdapter() {

                                        @Override
                                        public void internalFrameClosed(InternalFrameEvent arg0) {
                                            barPanel.close();
                                        }
                                    });

                                    String sensorId = getTagValue("sensor", barPanelElement);
                                    Sensor sensor = Main.findSensorByStringId(sensorId);
                                    barPanel.setSensor(sensor);

                                    mainForm.addVisualElement(topFrame);
                                    mainForm.getDesktopPanel().add(topFrame, 0);
                                    topFrame.setVisible(true);

                                }
                            });
                        }
                    }

                    final NodeList graphPanelList = iFrameElemet.getElementsByTagName("graph-panel");

                    for (int j = 0; j < graphPanelList.getLength(); j++) {
                        final Node graphPanelNode = graphPanelList.item(j);
                        if ((graphPanelNode != null) && (graphPanelNode.getNodeType() == Node.ELEMENT_NODE)) {

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {

                                    final InternalFrameObservable iFrame = new InternalFrameObservable(null, true, true);
                                    iFrame.setBounds(x, y, width, height);
                                    iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));

                                    Element graphPanelEle = (Element) graphPanelNode;
                                    final GraphPanel graphPanel = new GraphPanel(iFrame);
                                    iFrame.setMinimumSize(new Dimension(360, 210));
                                    iFrame.add(graphPanel);
                                    iFrame.setTitle(bundle.getString("GRAPH"));

                                    iFrame.addInternalFrameListener(new InternalFrameAdapter() {

                                        @Override
                                        public void internalFrameClosed(InternalFrameEvent arg0) {
                                            graphPanel.close();
                                        }
                                    });

                                    Date leftDate = new Date(Long.parseLong(getTagValue("left-date", graphPanelEle)));
                                    Date rightDate = new Date(Long.parseLong(getTagValue("right-date", graphPanelEle)));
                                    double highValue = Double.parseDouble(getTagValue("high-value", graphPanelEle));
                                    double lowValue = Double.parseDouble(getTagValue("low-value", graphPanelEle));

                                    boolean autoShiftLeft = Boolean.parseBoolean(getTagValue("auto-shift-left", graphPanelEle));

                                    if (autoShiftLeft) {
                                        long timestamp = rightDate.getTime() - leftDate.getTime();
                                        rightDate = new Date();
                                        leftDate = new Date(rightDate.getTime() - timestamp);
                                    }

                                    graphPanel.getGraph().setDateRange(leftDate, rightDate);
                                    graphPanel.getGraph().setValueRange(lowValue, highValue);
                                    graphPanel.getGraph().setAutoShiftLeft(autoShiftLeft);

                                    NodeList sensorsList = iFrameElemet.getElementsByTagName("sensors");

                                    for (int k = 0; k < sensorsList.getLength(); k++) {
                                        Node sensorsNode = sensorsList.item(k);
                                        if (sensorsNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element sensorsEle = (Element) sensorsNode;

                                            NodeList sensorList = sensorsEle.getElementsByTagName("sensor");
                                            for( int l = 0; l < sensorList.getLength(); l++) {
                                                Node sensorNode = sensorList.item(l);
                                                if (sensorNode.getNodeType() == Node.ELEMENT_NODE) {

                                                    Element sensorEle = (Element) sensorNode;
                                                    String sensorId = getTagValue("id", sensorEle);
                                                    Color c = new Color(Integer.parseInt(getTagValue("color", sensorEle)));

                                                    Sensor sensor = Main.findSensorByStringId(sensorId);

                                                    graphPanel.addSensor(sensor);
                                                    graphPanel.getGraph().setSensorColor(sensor, c);
                                                }
                                            }
                                        }
                                    }

                                    mainForm.addVisualElement(iFrame);
                                    mainForm.getDesktopPanel().add(iFrame);
                                    iFrame.setVisible(true);
                                }
                            });
                        }
                    }

                    final NodeList tablePanelList = iFrameElemet.getElementsByTagName("table-panel");

                    for (int j = 0; j < tablePanelList.getLength(); j++) {
                        final Node tablePanelNode = tablePanelList.item(j);
                        if ((tablePanelNode != null) && (tablePanelNode.getNodeType() == Node.ELEMENT_NODE)) {

                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    final InternalFrameObservable iFrame = new InternalFrameObservable(null, true, true);
                                    iFrame.setBounds(x, y, width, height);
                                    iFrame.setFrameIcon(new ImageIcon("graphics/kni - ikona16.png"));

                                    Element tablePanelEle = (Element) tablePanelNode;
                                    final TablePanel tablePanel = new TablePanel(iFrame);
                                    iFrame.setMinimumSize(new Dimension(360, 210));
                                    iFrame.add(tablePanel);
                                    iFrame.setTitle(bundle.getString("TABLE"));

                                    iFrame.addInternalFrameListener(new InternalFrameAdapter() {

                                        @Override
                                        public void internalFrameClosed(InternalFrameEvent arg0) {
                                            tablePanel.close();
                                        }
                                    });

                                    int dateTimeType = Integer.parseInt(getTagValue("date-time-type", tablePanelEle));
                                    double highRange = Double.parseDouble(getTagValue("high-range", tablePanelEle));
                                    int lastSavedValue = Integer.parseInt(getTagValue("last-saved-value", tablePanelEle));
                                    int lastTimespan = Integer.parseInt(getTagValue("last-timespan", tablePanelEle));
                                    int lastTimespanType = Integer.parseInt(getTagValue("last-timespan-type", tablePanelEle));
                                    int limit = Integer.parseInt(getTagValue("limit", tablePanelEle));
                                    double lowRange = Double.parseDouble(getTagValue("low-range", tablePanelEle));
                                    int orderBy = Integer.parseInt(getTagValue("order-by", tablePanelEle));
                                    Date periodHighDate = new Date(Long.parseLong(getTagValue("period-high-date", tablePanelEle)));
                                    Date periodLowDate = new Date(Long.parseLong(getTagValue("period-low-date", tablePanelEle)));

                                    TableSettingsDialog tsd = tablePanel.getTableSettingsDialog();
                                    tsd.setDateTimeType(dateTimeType);
                                    tsd.setHighRange(highRange);
                                    tsd.setLastSavedValue(lastSavedValue);
                                    tsd.setLastTimespan(lastTimespan);
                                    tsd.setLastTimespanType(lastTimespanType);
                                    tsd.setLimit(limit);
                                    tsd.setLowRange(lowRange);
                                    tsd.setOrderBy(orderBy);
                                    tsd.setPeriodHighDate(periodHighDate);
                                    tsd.setPeriodLowDate(periodLowDate);

                                    NodeList sensorsList = iFrameElemet.getElementsByTagName("sensors");

                                    for (int k = 0; k < sensorsList.getLength(); k++) {
                                        Node sensorsNode = sensorsList.item(k);
                                        if (sensorsNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element sensorsEle = (Element) sensorsNode;

                                            NodeList sensorList = sensorsEle.getElementsByTagName("sensor");
                                            for(int l = 0; l < sensorList.getLength(); l++ ) {
                                                Node sensorNode = sensorList.item(l);
                                                if (sensorNode.getNodeType() == Node.ELEMENT_NODE) {

                                                    Element sensorEle = (Element) sensorNode;
                                                    String sensorId = getTagValue("id", sensorEle);

                                                    Sensor sensor = Main.findSensorByStringId(sensorId);

                                                    tablePanel.addSensor(sensor);
                                                }
                                            }
                                        }
                                    }

                                    mainForm.addVisualElement(iFrame);
                                    mainForm.getDesktopPanel().add(iFrame);
                                    iFrame.setVisible(true);
                                }
                            });
                        }
                    }
                }

                mainForm.setBounds(new Rectangle(xMf, yMf, widthMf, heightMf));
                mainForm.repaint();
            }
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    public static void load(String fileName) {
        load(fileName, MainForm.getInstance());
    }

     public static void load(MainForm mainForm) {
        load("default.utw", mainForm);
    }

    public static void clear() {
        MainForm mf = MainForm.getInstance();
        List visualElements = new ArrayList();
        visualElements.addAll(mf.getVisualElemets());

        for (Object obj : visualElements) {
            if (obj instanceof JInternalFrame) {
                JInternalFrame iFrame = (JInternalFrame) obj;
                List<Component> components = new ArrayList();
                components.addAll(Arrays.asList(iFrame.getContentPane().getComponents()));
                for (Component c : components) {
                    if (c instanceof BarPanel) {
                        BarPanel barPanel = (BarPanel) c;
                        barPanel.close();
                    } else if (c instanceof GraphPanel) {
                        GraphPanel graphPanel = (GraphPanel) c;
                        graphPanel.close();
                    }
                }
                iFrame.setVisible(false);
                iFrame.dispose();
            } else if (obj instanceof JDialog) {
                JDialog dialog = (JDialog) obj;
                List<Component> components = new ArrayList();
                components.addAll(Arrays.asList(dialog.getContentPane().getComponents()));
                for (Component c : components) {
                    if (c instanceof BarPanel) {
                        BarPanel barPanel = (BarPanel) c;
                        barPanel.close();
                    }
                }
                dialog.setVisible(false);
            }
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        if (eElement.getElementsByTagName(sTag).getLength() > 0) {
            NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

            Node nValue = (Node) nlList.item(0);

            return nValue.getNodeValue();
        }
        return null;
    }
}
