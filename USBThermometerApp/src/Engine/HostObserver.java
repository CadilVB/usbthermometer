package Engine;

import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import USBThermometerLib.Device;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.A4EDC23C-DD8E-F710-0831-7AB30107D065]
// </editor-fold> 
public interface HostObserver {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.93193CF3-84D5-0536-F83F-93F80C7D95C2]
    // </editor-fold> 
    public void notiffyNewSample (Host host, Sample sample);

    public void notiffySensorUpdated(Host host, Sensor sensor);

    public void notiffyDeviceUpdated (Host host, Device device);

    public void notiffyDeviceAdd (Host host, Device device);
    
    public void notiffyDeviceRemoved (Host host, Device device);

}

