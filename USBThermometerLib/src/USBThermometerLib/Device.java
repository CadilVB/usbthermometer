package USBThermometerLib;

import USBThermometerLib.ExceptionErrors.DeviceError;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.3A08588E-FA22-188D-53DC-C94671101ED7]
// </editor-fold> 
public abstract class Device implements AutoCloseable {

    protected Date startConversionDate;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.B3E193BE-D6C9-3A2C-13A4-9BFAD818D9A7]
    // </editor-fold> 
    protected int hwHandle;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.918F54DE-5BAE-A3DF-E130-ADDA0380F6E1]
    // </editor-fold> 
    protected List<Sensor> sensors;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.2EC5572A-0A63-E99F-75A6-C805C944E05F]
    // </editor-fold> 
    protected Driver driver;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.F3AB83A7-C480-2BC1-AAB2-23EEDA31D549]
    // </editor-fold> 
    protected String serialNumber;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.B242DFA0-F849-4F06-5EB7-3BB950B5E60D]
    // </editor-fold> 
    protected Device (int hwHandle, Driver driver) throws DeviceError {
        this.hwHandle = hwHandle;
        this.driver = driver;
        sensors = new ArrayList<Sensor>();
    }

    public Driver getDriver() {
        return driver;
    }

    public int getHwHandle() {
        return hwHandle;
    }

    public Date getStartConversionDate() {
        return startConversionDate;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.780E8965-5B19-7E15-74C4-5FD0EE11D205]
    // </editor-fold> 
    public void searchSensors () throws DeviceError {        
        state = DeviceState.SEARCHSENSORS;
        doSearchSensors();
        state = DeviceState.RUNNABLE;
    }

    public abstract void doSearchSensors() throws DeviceError;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.36A9B632-1229-C2FA-FCDC-71E2AE78CFF8]
    // </editor-fold> 
    public List<Sensor> getSensors () {
        return sensors;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.FD229CBC-059A-CD7C-11E1-748E82DA3D04]
    // </editor-fold> 
    public void startConversion () throws DeviceError {
        startConversionDate = new Date();
        doConversion();
    }

    public void close() throws DeviceError {
        driver.CloseDevice(hwHandle);
        state = DeviceState.STOP;
    }

    protected abstract void doConversion() throws DeviceError;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.ABBB2BD6-98D6-8346-2464-3E520639DDEC]
    // </editor-fold> 
    public String getSerialNumber () {
        return serialNumber;
    }

    public DeviceState getState() {
        return state;
    }

    @Override
    public String toString() {
        return serialNumber;
    }

    protected DeviceState state = DeviceState.STOP;
}

