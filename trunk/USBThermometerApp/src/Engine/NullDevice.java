/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Engine;

import USBThermometerLib.Device;
import USBThermometerLib.Driver;
import USBThermometerLib.ExceptionErrors.DeviceError;

/**
 *
 * @author pawelkn
 */
public class NullDevice extends Device {


    protected NullDevice(int hwHandle, Driver driver, Host host) throws DeviceError {
        super(hwHandle, driver);
    }

    @Override
    public void doSearchSensors() throws DeviceError {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void doConversion() throws DeviceError {
        throw new UnsupportedOperationException("Not supported");
    }


}
