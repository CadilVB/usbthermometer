package USBThermometerLib;

import USBThermometerLib.ExceptionErrors.CrcError;
import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.ExceptionErrors.DeviceNotSupported;
import USBThermometerLib.ExceptionErrors.SearchError;
import java.io.File;


public class USBThermometerLib {

    public native int[] GetDevicesIds() throws DeviceError;

    public native long OpenDevice(int devId) throws DeviceError;

    public native void CloseDevice(long devHandle) throws DeviceError;

    public native String GetDeviceSerial(long devHandle) throws DeviceError;

    public native byte[][] GetSensorsIds(long devHandle) throws CrcError, SearchError, DeviceError;

    public native void StartConversion(long devHandle) throws DeviceError;

    public native double GetTemperture(long devHandle, byte[] sensorId) throws CrcError, DeviceNotSupported, DeviceError;

    static {
        LibraryLoader.LIBRARY_NAME = "usbThermometerLib";
        LibraryLoader.load();
    }                      
}
