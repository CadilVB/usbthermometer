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

    void CloseDevice(int devHandle) throws DeviceError;

    String GetDeviceSerial(int devHandle) throws DeviceError;

    int[] GetDevicesIds() throws DeviceError;

    byte[][] GetSensorsIds(int devHandle) throws CrcError, SearchError, DeviceError;

    double GetTemperture(int devHandle, byte[] sensorId) throws CrcError, DeviceNotSupported, DeviceError;

    int OpenDevice(int devId) throws DeviceError;

    void StartConversion(int devHandle) throws DeviceError;
    
}
