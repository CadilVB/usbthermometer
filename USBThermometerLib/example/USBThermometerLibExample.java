import USBThermometerLib.ExceptionErrors.*;
import USBThermometerLib.*;

class USBThermometerLibExample {

	private final Driver driver;

	public static void main(String args[]) {
		new USBThermometerLibExample();
	}
	
	public USBThermometerLibExample() {
		driver = USBThermometerDriver.getInstance();
	
		try {			
			while(true) {
				int[] devicesIds = driver.GetDevicesIds();					
				for( int deviceId : devicesIds ) {			
					DeviceThread deviceThread = new DeviceThread(deviceId);
					deviceThread.start();					
				}		
			}
		} catch (DeviceError ex) {
            ex.printStackTrace();
        }
	}
	
	private class DeviceThread extends Thread {
		
		private int hwHandle;
		private Device device;
		
		public DeviceThread(int deviceId) {
			try {
                int hwhandle = driver.OpenDevice(deviceId);
                device = new USBThermometerDevice( hwhandle, driver );						
            } catch (DeviceError ex) {
                ex.printStackTrace();
            }
		}
		
		@Override
		public void run() {
            try {
				System.out.println("Connected " + device.getSerialNumber());
				System.out.println("Search sensors...");				
			
                device.searchSensors();
				
				System.out.println("Found: ");				
				for( Sensor sensor : device.getSensors() ) {
					System.out.println("\t" + sensor.getStringId());
				}
				System.out.println();
				
                device.startConversion();
                Thread.sleep(800);	

                while(true) {
                    for( Sensor sensor : device.getSensors() ) {
                        Sample sample = sensor.getNewSample();
						Temperature temperature = (Temperature)sample;
						
						System.out.println( sensor.getStringId() + ": " + 
							temperature.getValue(Temperature.CELSIUS) + 
							temperature.getUnitString());
                    }
                    device.startConversion();
                    Thread.sleep(800);								
                }
            } catch (DeviceError ex) {
                System.out.println("Disconnected.");
            } catch (InterruptedException ex) {
				ex.printStackTrace();
			} finally {
				try {
					device.close();
				} catch (DeviceError ex) {
					//nothing can do
				}
            }			
		}		
	}
}