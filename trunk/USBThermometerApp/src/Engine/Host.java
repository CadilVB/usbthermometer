package Engine;

import USBThermometerLib.ExceptionErrors.DeviceError;
import USBThermometerLib.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.BDEFDFC0-60E7-F44E-01D9-B22689BCFD81]
// </editor-fold> 
public class Host implements Runnable {

    private final Thread mainThread;
    private boolean threadRun;
    private final List<HostObserver> hostObservers = Collections.synchronizedList( new ArrayList<HostObserver>() );
    private final Driver driver;
    private final List<DeviceThread> deviceThreads = Collections.synchronizedList( new ArrayList<DeviceThread>() );
    private NullDevice nullDevice;
    private int samplingRate = 1;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.3B08B086-1101-5CE8-817C-0F7D192E613B]
    // </editor-fold> 
    private final List<Device> devices = Collections.synchronizedList( new ArrayList<Device>() );

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.0C0061BD-4D9D-0AAE-5859-51630735C2C2]
    // </editor-fold> 
    private final Database database;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.C203ABAD-1FB1-4877-5ECB-C6838F09A698]
    // </editor-fold> 
    public Host (Database database) {
        this.database = database;

        driver = USBThermometerDriver.getInstance();
        mainThread = new Thread(this);
        try {
            nullDevice = new NullDevice(0, null, this);
        } catch (DeviceError ex) {
        }
    }    

    @Override
    public String toString() {
        return database.toString();
    }

    public void addHostObserver (HostObserver hostObserver) {
        synchronized(hostObservers) {
            if( !hostObservers.contains(hostObserver) ) {
                hostObservers.add(hostObserver);
            }
        }
    }

    public void removeHostObserver (HostObserver hostObserver) {
        synchronized(hostObservers) {
            hostObservers.remove(hostObserver);
        }
    }

    public void startOperation () {
        if(!threadRun) {            
            threadRun = true;
            mainThread.start();
        }
    }

    public void stopOperation () {
        threadRun = false;

        try {
            mainThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    public void updateSensor(Sensor sensor) {
        database.updateSensor(sensor);

        synchronized(hostObservers) {
            for(HostObserver hostObserver : hostObservers) {
                hostObserver.notiffySensorUpdated(this,sensor);
            }
        }
    }
    
    public Database getDatabase() {
        return database;
    }

    public List<Device> getDevices() {
        return devices;
    }

    @Override
    public void run() {
        for(Sensor sensor : database.getSensors()) {
            sensor.setDevice(nullDevice);
            nullDevice.getSensors().add(sensor);
        }
        devices.add(nullDevice);

        synchronized(hostObservers) {
            for(HostObserver hostObserver : hostObservers) {
                hostObserver.notiffyDeviceAdd(this,nullDevice);
                for(Sensor nullSensor : nullDevice.getSensors()) {
                    hostObserver.notiffySensorUpdated(this,nullSensor);
                }
            }
        }

        while(threadRun) {
            try {
                int[] devicesIds = driver.GetDevicesIds();
                for( int deviceId : devicesIds ) {
                    DeviceThread deviceThread = new DeviceThread(deviceId);
                    deviceThread.start();
                    deviceThreads.add(deviceThread);
                }
                List<Sensor> tempList = new ArrayList<>();
                tempList.addAll(nullDevice.getSensors());
                for(Sensor sensor : tempList) {
                    Sample s1 = database.getLastSample(sensor);
                    Sample s2 = sensor.getLastSample();
                    if( s2 != null ) {
                        if( (s1 != null) && (s1.getDateTimeCreation().getTime() > s2.getDateTimeCreation().getTime()) ) {
                            sensor.setLastSample(s1);
                            synchronized (hostObservers) {
                                for (HostObserver hostObserver : hostObservers) {
                                    hostObserver.notiffyNewSample(this,s1);
                                }
                            }
                        }
                    } else {
                        sensor.setLastSample(s1);
                    }
                }
            } catch (DeviceError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }

            Delay.Ms(1000);
        }
    }

    public int getSamplingRate() {
        return samplingRate / 1000;
    }

    public void setSamplingRate(int samplingRate) {
        if( samplingRate < 1 ) {
            samplingRate = 1;
        }
        this.samplingRate = samplingRate * 1000;
    }

    private class DeviceThread extends Thread {

        public DeviceThread(int deviceId) {
            try {
                int hwhandle = driver.OpenDevice(deviceId);
                device = new USBThermometerDevice( hwhandle, driver );
                devices.add(device);

                synchronized(hostObservers) {
                    for(HostObserver hostObserver : hostObservers) {
                        hostObserver.notiffyDeviceAdd(Host.this,device);
                    }
                }
            } catch (DeviceError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                device.searchSensors();
                device.startConversion();
                Delay.Ms(800);

                updateNullDevice();
                List<Sample> samples = new ArrayList<>();

                while(threadRun) {
                    Date start = new Date();
                    samples.clear();
                    for( Sensor sensor : device.getSensors() ) {
                        Sample sample = sensor.getNewSample();
                        if( sample != null ) {
                            samples.add(sample);
                            synchronized(hostObservers) {
                                for(HostObserver hostObserver : hostObservers) {
                                    hostObserver.notiffyNewSample(Host.this,sample);
                                }
                            }
                        }
                    }
                    database.addSamples(samples);
                    device.startConversion();
                    Date stop = new Date();
                    
                    long delay = samplingRate - (stop.getTime() - start.getTime());
                    delay = ( delay < 800 ? 800 : delay );
                    Delay.Ms((int)delay);
                }
            } catch (DeviceError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            } finally {
                finalizeDeviceRemove();
            }
        }

        private void updateNullDevice() {
             List<Sensor> nullDeviceSensorsCopy = new ArrayList<>();
             nullDeviceSensorsCopy.addAll(nullDevice.getSensors());
             List<Sensor> deviceSensorsCopy = new ArrayList<>();
             deviceSensorsCopy.addAll(device.getSensors());

             for(Sensor sensor : deviceSensorsCopy) {
                boolean newSensor = true;

                for(Sensor nullSensor : nullDeviceSensorsCopy) {
                    if( Arrays.equals(sensor.getId(),nullSensor.getId()) ) {
                        nullDevice.getSensors().remove(nullSensor);
                        device.getSensors().remove(sensor);
                        nullSensor.setDevice(sensor.getDevice());
                        device.getSensors().add(nullSensor);
                        newSensor = false;
                    }
                }
                
                if( newSensor ) {
                    database.addSensor(sensor);
                }
            }

             synchronized(hostObservers) {
                for(HostObserver hostObserver : hostObservers) {
                    hostObserver.notiffyDeviceUpdated(Host.this,nullDevice);
                    hostObserver.notiffyDeviceUpdated(Host.this,device);
                }
             }
        }

        private void finalizeDeviceRemove() {
            for(Sensor sensor : device.getSensors()) {
                sensor.setDevice(nullDevice);
                nullDevice.getSensors().add(sensor);
            }
            device.getSensors().clear();

            synchronized(hostObservers) {
                for(HostObserver hostObserver : hostObservers) {
                    hostObserver.notiffyDeviceRemoved(Host.this,device);
                    hostObserver.notiffyDeviceUpdated(Host.this,nullDevice);
                }
            }

            for(int i = 0; i < 15000; i++) {
                Delay.Ms(1);
            }

            try {
                device.close();
            } catch (DeviceError ex) {
                Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
            }
            devices.remove(device);
        }

        private Device device;
    }
}

