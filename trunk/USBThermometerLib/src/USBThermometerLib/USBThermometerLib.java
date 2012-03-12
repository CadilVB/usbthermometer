package USBThermometerLib;

import USBThermometerLib.ExceptionErrors.CrcError;
import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.DeviceNotSupported;
import USBThermometerLib.ExceptionErrors.SearchError;
import java.io.File;


public class USBThermometerLib {

    public native int[] GetDevicesIds() throws DeviceError;

    public native int OpenDevice(int devId) throws DeviceError;

    public native void CloseDevice(int devHandle) throws DeviceError;

    public native String GetDeviceSerial(int devHandle) throws DeviceError;

    public native byte[][] GetSensorsIds(int devHandle) throws CrcError, SearchError, DeviceError;

    public native void StartConversion(int devHandle) throws DeviceError;

    public native double GetTemperture(int devHandle, byte[] sensorId) throws CrcError, DeviceNotSupported, DeviceError;

    static {
        LibraryLoader.LIBRARY_NAME = "usbThermometerLib";
        LibraryLoader.load();
    }                      
}
