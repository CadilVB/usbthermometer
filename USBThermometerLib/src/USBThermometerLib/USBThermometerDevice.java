package USBThermometerLib;

// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 

import USBThermometerLib.ExceptionErrors.CrcError;
import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.SearchError;
import java.util.logging.Level;
import java.util.logging.Logger;

// #[regen=yes,id=DCE.211F40B8-C5EB-1DF2-DC6F-34B935BB0663]
// </editor-fold> 
public class USBThermometerDevice extends Device {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.576CF7C6-584F-04AC-68C2-07B649D58C08]
    // </editor-fold> 
    public USBThermometerDevice (int hwHandle, Driver driver) throws DeviceError {
        super(hwHandle, driver);

        serialNumber = driver.GetDeviceSerial(hwHandle);
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.D5963B51-9148-AB4E-CC4F-BCDAF08CCCCB]
    // </editor-fold> 
    @Override
    public void doSearchSensors () throws DeviceError {
        byte[][] romIds = null;
        while( romIds == null ) {
            try {
                romIds = driver.GetSensorsIds(hwHandle);
            } catch (CrcError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            } catch (SearchError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }
        for(byte[] romId : romIds) {
            if( romId[0] == 0x10 ) {
                Sensor sensor = new DS18S20(romId,this);
                sensor.name = Sensor.convertByteToHex(romId);
                sensors.add(sensor);
            } else if( romId[0] == 0x28 ) {
                Sensor sensor = new DS18B20(romId,this);
                sensor.name = Sensor.convertByteToHex(romId);
                sensors.add(sensor);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.E734344D-B785-1D0B-7D78-F9385D2480C0]
    // </editor-fold> 
    @Override
    protected void doConversion () throws DeviceError {
       driver.StartConversion(hwHandle);
    }

    public String toString() {
        return "USBThermometer (" + getSerialNumber() + ")";
    }
}

