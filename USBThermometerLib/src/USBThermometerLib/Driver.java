package USBThermometerLib;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.SearchError;
import USBThermometerLib.ExceptionErrors.DeviceNotSupported;
import USBThermometerLib.ExceptionErrors.CrcError;

/**
 *
 * @author pawelkn
 */
public interface Driver {

    void CloseDevice(long devHandle) throws DeviceError;

    String GetDeviceSerial(long devHandle) throws DeviceError;

    int[] GetDevicesIds() throws DeviceError;

    byte[][] GetSensorsIds(long devHandle) throws CrcError, SearchError, DeviceError;

    double GetTemperture(long devHandle, byte[] sensorId) throws CrcError, DeviceNotSupported, DeviceError;

    long OpenDevice(int devId) throws DeviceError;

    void StartConversion(long devHandle) throws DeviceError;
    
}
