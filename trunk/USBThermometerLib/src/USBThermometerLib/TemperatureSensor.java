/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package USBThermometerLib;

import USBThermometerLib.ExceptionErrors.CrcError;
import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.DeviceNotSupported;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paweł
 */
public abstract class TemperatureSensor extends Sensor {
    private double lastTemperature = 0.0;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.F17DAAD6-2ABD-1416-7088-ABDF1D82001A]
    // </editor-fold> 
    public TemperatureSensor (byte[] Id, Device device) {
        super(Id,device);
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.E0C05E0C-14C6-8A27-8AC6-6E27AD57EC75]
    // </editor-fold> 
    @Override
    public Sample getNewSample () throws DeviceError {
        Driver driver = device.getDriver();
        Double temperature = null;
        Sample sample = null;
        int timeout = 10;

        if( driver != null ) {
            try {
                while( ( temperature == null ) && ( timeout-- > 0 ) ){
                    try {
                        temperature = trunc(driver.GetTemperture(device.getHwHandle(), Id));

                        if( ( temperature == 85 ) && ( Math.abs(lastTemperature - temperature) > 1 ) ) {
                            temperature = null;
                        } else {
                            lastTemperature = temperature;
                        }
                    } catch (CrcError ex) {
                    }
                }
            } catch (DeviceNotSupported ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
            if( temperature != null ) {
                sample = new Temperature(this,temperature,device.getStartConversionDate());
            }
            lastSample = sample;
        }
        return sample;
    }

    @Override
    public String getMedium() {
        return "TEMPERATURE";
    }

    private double trunc(double x) {
        double invBase = 10;
        if ( x > 0 )
            return Math.floor(x * invBase)/invBase;
        else
            return Math.ceil(x * invBase)/invBase;
    }
}
