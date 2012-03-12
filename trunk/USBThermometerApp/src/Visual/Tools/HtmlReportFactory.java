/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import Engine.Database;
import Main.Main;
import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.Temperature;
import Tools.ConfigurationManager;
import Tools.CharsetControl;
import Visual.Graph.Graph;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author pawelkn
 */
public class HtmlReportFactory {
    
    public static final File REPORT_FILE = new File("./report/report.html");
    public static final File CSS_FILE = new File("./report/style.css");
    public static final File GRAPH_FILE = new File("./report/graph.png");

    private static File TEMPLATE_FILE;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final List<Sensor> sensors;
    private final Date startDate;
    private final Date stopDate;

    private final Hashtable minValues = new Hashtable();
    private final Hashtable maxValues = new Hashtable();

    private static ResourceBundle bundle;

    public HtmlReportFactory(List<Sensor> sensors, Date startDate, Date stopDate) {
        this.sensors = sensors;
        this.startDate = startDate;
        this.stopDate = stopDate;

        Locale locale = new Locale( ConfigurationManager.load().getLocale() );
        ResourceBundle.clearCache();
        bundle = ResourceBundle.getBundle("Bundle", locale, new CharsetControl());

        TEMPLATE_FILE = new File( bundle.getString("TEMPLATE_FILE") );
    }

    private void buildGraph() {
        BufferedImage im = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        Graph graph = new Graph(im);
        graph.setDateRange(startDate, stopDate);
        for(Sensor sensor : sensors) {
            graph.addSensor(sensor);
        }
        graph.autorange();
        graph.paint();

        try {
            ImageIO.write(im, "png", GRAPH_FILE);
        } catch (IOException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    private String parseDocument(String document) {
        StringBuilder result = new StringBuilder();
        String[] foreachs = document.split("[{]+foreach sensors+[}]|[{]+[/]+foreach+[}]");

        for( int i = 0; i < foreachs.length; i++ ) {
            if( (i % 2) == 0 ) {
                result.append( parse( foreachs[i], null ));
            } else {
                for(Sensor sensor : sensors ) {
                    result.append( parse( foreachs[i], sensor ));
                }
            }
        }

        return result.toString();
    }

    private String parse(String document, Sensor sensor) {
        StringBuilder result = new StringBuilder();
        String[] parts = document.split("[{}]");

        for (String part : parts) {
            if (part.startsWith("sensor")) {
                String[] valueParts = part.split("[.]");
                if (valueParts.length > 1) {
                    if ("name".equals(valueParts[1])) {
                        result.append(sensor);
                    } else if ("max".equals(valueParts[1])) {
                        double maxValue = Main.getLocalhost().getDatabase()
                                .getMaximumSample(sensor, startDate, stopDate).getValue();
                        maxValues.put(sensor, maxValue);
                        result.append(maxValue);
                        result.append("&deg;");
                        result.append(Temperature.getShortUnitString());
                    } else if ("min".equals(valueParts[1])) {
                        double minValue = Main.getLocalhost().getDatabase()
                                .getMinimumSample(sensor, startDate, stopDate).getValue();
                        minValues.put(sensor, minValue);
                        result.append(minValue);
                        result.append("&deg;");
                        result.append(Temperature.getShortUnitString());
                    } else if ("avg".equals(valueParts[1])) {
                        double avgValue = roundDecimal(Main.getLocalhost().getDatabase()
                                .getAverangeSample(sensor, startDate, stopDate).getValue(), 10);
                        result.append(avgValue);
                        result.append("&deg;");
                        result.append(Temperature.getShortUnitString());
                    } else if ("diff".equals(valueParts[1])) {
                        double maxValue = (Double) (maxValues.get(sensor));
                        double minValue = (Double) (minValues.get(sensor));
                        maxValues.remove(sensor);
                        minValues.remove(sensor);
                        result.append(roundDecimal(maxValue - minValue, 10));
                        result.append("&deg;");
                        result.append(Temperature.getShortUnitString());
                    } else if ("trend".equals(valueParts[1])) {
                        long timespan = (stopDate.getTime() - startDate.getTime()) / 1000;
                        List<Sample> samples = Main.getLocalhost().getDatabase()
                                .getSamples(sensor, startDate, stopDate, timespan, 1000, Database.ASC);
                        double trend = trend(samples);

                        if (Math.abs(trend) >= 0.1) {
                            result.append(roundDecimal(trend, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / ms");
                        } else if (Math.abs(trend) >= (0.1 / (1000))) {
                            result.append(roundDecimal(trend * 1000, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / s");
                        } else if (Math.abs(trend) >= (0.1 / (1000 * 60))) {
                            result.append(roundDecimal(trend * 1000 * 60, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / min");
                        } else if (Math.abs(trend) >= (0.1 / (1000 * 60 * 60))) {
                            result.append(roundDecimal(trend * 1000 * 60 * 60, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / h");
                        } else if (Math.abs(trend) >= (0.1 / (1000 * 60 * 60 * 24))) {
                            result.append(roundDecimal(trend * 1000 * 60 * 60 * 24, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / day");
                        } else {
                            result.append(roundDecimal(trend * 1000 * 60 * 60 * 24 * 30, 1000));
                            result.append("&deg;");
                            result.append(Temperature.getShortUnitString());
                            result.append(" / mounth");
                        }
                    }
                }
            } else if (part.startsWith("date")) {
                String[] valueParts = part.split("[.]");
                if (valueParts.length > 1) {
                    if ("begin".equals(valueParts[1])) {
                        result.append(dateFormat.format(startDate));
                    } else if ("end".equals(valueParts[1])) {
                        result.append(dateFormat.format(stopDate));
                    }
                }
            } else {
                result.append(part);
            }
        }
        return result.toString();
    }

    public String build() {
        buildGraph();

        StringBuilder text = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileInputStream(TEMPLATE_FILE), bundle.getString("CHARSET"));
            try {
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine());
                }
            } finally {
                scanner.close();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }

        String parsedDocument = parseDocument(text.toString());

        try {
            FileWriter fstream = new FileWriter(REPORT_FILE);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(parsedDocument);
            out.close();
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
        return parsedDocument;
    }

    private double trend(List<Sample> samples) {
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        int n = samples.size();

        for(Sample sample : samples) {
            double x = sample.getDateTimeCreation().getTime();
            double y = sample.getValue();

            sumX += x;
            sumY += y;
            sumXY += (x * y);
            sumX2 += (x * x);
        }

        double a = ( n * sumXY - sumX * sumY ) / ( n * sumX2 - sumX * sumX );
        return a;
    }

    private static double roundDecimal(double d, int base) {
        double result = d * base;
        result = Math.round(result);
        result = result / base;
        return result;
    }
}
