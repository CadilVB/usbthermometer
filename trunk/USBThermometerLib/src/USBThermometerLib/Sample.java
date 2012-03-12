package USBThermometerLib;

import java.util.Date;


// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.37ACCE75-9E01-8CC0-7159-3B811141FCE4]
// </editor-fold> 
public abstract class Sample {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.52B9185D-0D64-2335-0C82-64EB03140950]
    // </editor-fold> 
    protected Sensor sensor;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.A03C95DF-B4D7-2854-CB68-CAA6F2415779]
    // </editor-fold> 
    protected double value;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.0AAD57EB-E2BF-2C58-C589-A956456F5ADD]
    // </editor-fold> 
    protected Date dateTimeCreation;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.95E15EBA-E767-C840-F413-EBE34998EBD2]
    // </editor-fold> 
    protected Sample (Sensor sensor, double value, Date dateTimeCreation) {
        this.sensor = sensor;
        this.value = value;
        this.dateTimeCreation = dateTimeCreation;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.7E430E15-6AEB-DBD9-2920-F526537AF3E8]
    // </editor-fold> 
    public Date getDateTimeCreation () {
        return dateTimeCreation;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.F6D0A092-A4DD-DC54-216D-81843FA41B7E]
    // </editor-fold> 
    public Sensor getSensor () {
        return sensor;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.94CAA924-F9AB-A1C2-7DAC-5548F430AE50]
    // </editor-fold> 
    public double getValue () {
        return value;
    }

}

