package USBThermometerLib;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import USBThermometerLib.ExceptionErrors.CrcError;
import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.DeviceNotSupported;
import USBThermometerLib.ExceptionErrors.SearchError;

/**
 *
 * @author pawelkn
 */
public class USBThermometerDriver implements Driver {
    
    public static USBThermometerDriver getInstance() {
        if( instance == null ) {
            instance = new USBThermometerDriver();
        }
        return instance;
    }

    @Override
    public synchronized void CloseDevice(int devHandle) throws DeviceError {
            usbThermometerLib.CloseDevice(devHandle);
    }

    @Override
    public synchronized String GetDeviceSerial(int devHandle) throws DeviceError {
            return usbThermometerLib.GetDeviceSerial(devHandle);
    }

    @Override
    public synchronized int[] GetDevicesIds() throws DeviceError {
            return usbThermometerLib.GetDevicesIds();
    }

    @Override
    public synchronized byte[][] GetSensorsIds(int devHandle) throws CrcError, SearchError, DeviceError {
            return usbThermometerLib.GetSensorsIds(devHandle);
    }

    @Override
    public synchronized double GetTemperture(int devHandle, byte[] sensorId) throws CrcError, DeviceNotSupported, DeviceError {
            return usbThermometerLib.GetTemperture(devHandle, sensorId);
    }

    @Override
    public synchronized int OpenDevice(int devId) throws DeviceError {
            return usbThermometerLib.OpenDevice(devId);
    }

    @Override
    public synchronized void StartConversion(int devHandle) throws DeviceError {
            usbThermometerLib.StartConversion(devHandle);
    }
    
    private USBThermometerDriver() {
        usbThermometerLib = new USBThermometerLib();
    }    
    
    private static USBThermometerDriver instance;
    private USBThermometerLib usbThermometerLib;
}
