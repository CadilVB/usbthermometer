package Engine;

import USBThermometerLib.Sample;
import USBThermometerLib.Sensor;
import java.util.Date;
import java.util.List;

// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.651EDCA4-24D6-D4A8-9F31-F58AE9EFAE3E]
// </editor-fold> 
public interface Database {

    public static final int ASC = 0;
    public static final int DESC = 1;

    public void initialize();

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.2F50B067-F74E-B97E-7D89-F3850224A5C5]
    // </editor-fold> 
    public void addSample (Sample sample);

    public void addSamples (List<Sample> samples);

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.C829C7B4-8BE4-81B0-F066-A3C72CADC427]
    // </editor-fold> 
    public Sample getLastSample (Sensor sensor);

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.23C9DC97-93AF-BF6C-41BC-FB04A9B070E2]
    // </editor-fold> 
    public List<Sample> getSamples (Sensor sensor, Date startDateTime, Date stopDateTime, int limit, int orderBy);

    public List<Sample> getSamples (Sensor sensor, Date startDateTime, Date stopDateTime, int limit, int orderBy, double lowRange, double highRange );

    public List<Sample> getSamples (Sensor sensor, Date startDateTime, Date stopDateTime, long timespan, int limit, int orderBy);

    public Sample getMaximumSample (Sensor sensor, Date startDateTime, Date stopDateTime);

    public Sample getMinimumSample (Sensor sensor, Date startDateTime, Date stopDateTime);

    public Sample getAverangeSample (Sensor sensor, Date startDateTime, Date stopDateTime);

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.1FDA6AD6-66A8-A1A2-CA8A-406F820E013B]
    // </editor-fold> 
    public void addSensor (Sensor sensor);

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.C0F259E5-8545-CA09-6F95-04EA1624B758]
    // </editor-fold> 
    public void removeSensor (Sensor sensor);

    public void updateSensor (Sensor sensor);

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.1457DDCF-293A-F392-5AA5-C84910A6F0AC]
    // </editor-fold> 
    public List<Sensor> getSensors ();
}

