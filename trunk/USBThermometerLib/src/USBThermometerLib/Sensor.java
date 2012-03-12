package USBThermometerLib;

// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 

import USBThermometerLib.ExceptionErrors.DeviceError;

// #[regen=yes,id=DCE.05156D3F-D14E-01AF-DBCF-8DB94D04343D]
// </editor-fold> 
public abstract class Sensor {

    protected Sample lastSample;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.50DD33A8-2537-53A2-5089-4723F0418E4B]
    // </editor-fold> 
    protected byte[] Id;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.F3C0E546-5F0E-93BB-D212-13A68A303553]
    // </editor-fold> 
    protected Device device;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.A03B0F88-74CE-C294-6808-420DDEB65CD1]
    // </editor-fold> 
    protected String name;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.1BF11E95-3FF6-C77F-0DF9-CDAE20F7CFFD]
    // </editor-fold> 
    protected Sensor (byte[] Id, Device device) {
        this.Id = Id;
        this.device = device;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.65829056-E0AB-CEE9-7C6F-41552FA2DC64]
    // </editor-fold> 
    public byte[] getId () {
        return Id;
    }

    public String getStringId() {
        return convertByteToHex(Id);
    }

    public long getLongId() {
        return convertByteToLong(Id);
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.58A45645-3802-69BD-DF10-40AA28621DC9]
    // </editor-fold> 
    public abstract Sample getNewSample () throws DeviceError;

    public void setLastSample(Sample sample) {
        lastSample = sample;
    }

    public Sample getLastSample() {
        return lastSample;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.7FA408F4-B659-AB71-2CD0-FC399752D9B9]
    // </editor-fold> 
    public String getName () {
        return name;
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.00C697E7-7B69-B4E5-9F1D-F8637019CB6E]
    // </editor-fold> 
    public void setName (String val) {
        this.name = val;
    }

    public abstract String getMedium();

    @Override
    public String toString() {
        return getName();
    }

    public static long convertByteToLong(byte[] data) {
        int i = 0;
        long result = 0L;

        while( (i < data.length) && (i < 8) ) {
            result |= (((long)(data[i] & 0x000000ff)) << (7 - i)*8);
            i++;
        }
        return result;
    }

    public static byte[] convertLongToByte(long data) {
        byte[] result = new byte[8];

        for(int i = 0; i < 8; i++) {
            result[i] |= (byte)(data >> (7 - i)*8);
        }
        return result;
    }

    public static String convertByteToHex(byte[] data) {
        StringBuilder s = new StringBuilder(2 * data.length);
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xff;
            s.append((char)Hexchars[v >> 4]);
            s.append((char)Hexchars[v & 0xf]);
        }
        return s.toString();
    }

    private static final byte[] Hexchars = {
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
    };
}

