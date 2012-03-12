package USBThermometerLib;

import java.util.Date;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.5B40BF83-E92A-1CC6-209D-129BF19AAC8A]
// </editor-fold> 
public class Temperature extends Sample {

    public static final int CELSIUS = 0;
    public static final int FAHRENHEIT = 1;

    private static int unit;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.D829D378-5644-8615-DD4D-A487EE03EF65]
    // </editor-fold> 
    public Temperature (Sensor sensor, double value, Date dateTimeCreation) {
        super(sensor,value,dateTimeCreation);
    }

    public double getValue(int unit) {
        if( unit == CELSIUS ) {
            return value;
        } else {
            return roundOneDecimal(value * 9 / 5 + 32);
        }
    }

    @Override
    public double getValue() {
        return getValue(unit);
    }

    public static int getUnit() {
        return unit;
    }

    public static void setUnit(int unit) {
        Temperature.unit = unit;
    }

    public static String getUnitString() {
        return "\u00B0" + (unit == CELSIUS ? "C" : "F" );
    }

    public static String getShortUnitString() {
        return (unit == CELSIUS ? "C" : "F" );
    }


    private double roundOneDecimal(double d) {
        double result = d * 10;
        result = Math.round(result);
        result = result / 10;
        return result;
    }
}

