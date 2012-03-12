/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Triggers;

import Visual.Triggers.RS232TriggerConfigurationPanel;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author pawelkn
 */
public class RS232Trigger extends Trigger {

    public static final int PINSTATE_DONTTOUCH = 0;
    public static final int PINSTATE_SET0 = 1;
    public static final int PINSTATE_SET1 = 2;

    private RS232TriggerConfigurationPanel c;

    private String comPort = "COM1";
    private int dtrState;
    private int rtsState;
    private boolean sendCommand;
    private byte[] command;
    private int baudRate = 9600;
    private int bits = 3;
    private int stopBits = 2;
    private int parity = 2;

    private static final List<CommPort> commPorts = Collections.synchronizedList(new ArrayList<CommPort>());

    public RS232Trigger() {
        super();

        setName("RS232 Trigger");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if( enabled ) {
            openCommPort();
        } else {
            closeCommPort();
        }
    }

    @Override
    public void setTriggersManager(TriggersManager triggersManager) {
        super.setTriggersManager(triggersManager);

        if( triggersManager == null ) {
            closeCommPort();
        }
    }

    private void openCommPort() {
        if(comPort != null) {
            synchronized (commPorts) {
                try {
                    CommPort toAdd = null;
                    for (CommPort commPort : commPorts) {
                        if (commPort.getName().indexOf(comPort) != -1) {
                            toAdd = commPort;
                            break;
                        }
                    }
                    if( toAdd == null ) {
                        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(comPort);
                        if (portIdentifier.isCurrentlyOwned()) {
                            System.out.println("Error: Port is currently in use");
                        } else {
                            Thread.sleep(100);
                            toAdd = portIdentifier.open(this.getClass().getName(), 2000);
                        }
                    }
                    commPorts.add(toAdd);
                } catch (Exception ex) {
                    Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
                }
            }
        }
    }

    private void closeCommPort() {
        synchronized (commPorts) {
            CommPort toRemove = null;
            for (CommPort commPort : commPorts) {
                if (commPort.getName().indexOf(comPort) != -1) {
                    toRemove = commPort;
                    break;
                }
            }
            if (toRemove != null) {
                toRemove.close();
                commPorts.remove(toRemove);
            }
        }
    }

    private CommPort getCommPort() {
        synchronized (commPorts) {
            for (CommPort commPort : commPorts) {
                if (commPort.getName().indexOf(comPort) != -1) {
                    return commPort;
                }
            }
            return null;
        }
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }

    public int getDtrState() {
        return dtrState;
    }

    public void setDtrState(int dtrState) {
        this.dtrState = dtrState;
    }

    public int getRtsState() {
        return rtsState;
    }

    public void setRtsState(int rtsState) {
        this.rtsState = rtsState;
    }

    public boolean isSendCommand() {
        return sendCommand;
    }

    public void setSendCommand(boolean sendCommand) {
        this.sendCommand = sendCommand;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public String toString() {
        return getName();
    }

    private int getSerialBits(int bits) {
        switch(bits) {
            case 0:
                return SerialPort.DATABITS_5;
            case 1:
                return SerialPort.DATABITS_6;
            case 2:
                return SerialPort.DATABITS_7;
            default:
                return SerialPort.DATABITS_8;
        }
    }

    private int getSerialStopBits(int bits) {
        switch(bits) {
            case 0:
                return SerialPort.STOPBITS_1;
            case 1:
                return SerialPort.STOPBITS_1_5;
            default:
                return SerialPort.STOPBITS_2;
        }
    }

    private int getSerialParity(int parity) {
        switch(parity) {
            case 0:
                return SerialPort.PARITY_EVEN;
            case 1:
                return SerialPort.PARITY_MARK;
            case 2:
                return SerialPort.PARITY_NONE;
            case 3:
                return SerialPort.PARITY_ODD;
            case 4:
                return SerialPort.PARITY_SPACE;
            default:
                return SerialPort.PARITY_NONE;
        }
    }

    @Override
    public JPanel getConfigurationPanel() {
        c = new RS232TriggerConfigurationPanel();
        c.setComPort(comPort);
        c.setDtrState(dtrState);
        c.setRtsState(rtsState);
        c.setSendCommand(sendCommand);
        c.setCommand(command);
        c.setBaudRate(baudRate);
        c.setBits(bits);
        c.setStopBits(stopBits);
        c.setParity(parity);
        return c;
    }

    @Override
    public void onChange() {
    }

    @Override
    public void onEnter() {
        try {
            CommPort commPort = getCommPort();
            if ((commPort != null ) && (commPort instanceof SerialPort)) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate, getSerialBits(bits), getSerialStopBits(stopBits), getSerialParity(parity));

                if( dtrState == PINSTATE_SET0 ) {
                    serialPort.setDTR(false);
                } else if ( dtrState == PINSTATE_SET1 ) {
                    serialPort.setDTR(true);
                }

                if( rtsState == PINSTATE_SET0 ) {
                    serialPort.setRTS(false);
                } else if ( rtsState == PINSTATE_SET1 ) {
                    serialPort.setRTS(true);
                }

                if( sendCommand ) {
                    OutputStream out = serialPort.getOutputStream();
                    out.write(command);
                }
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        } catch (Exception ex) {
            Logger.getLogger("USBThermometer").log(Level.ALL, null, ex);
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public void updateConfiguration() {
        if( c != null ) {
            comPort = c.getComPort();
            dtrState = c.getDtrState();
            rtsState = c.getRtsState();
            sendCommand = c.getSendCommand();
            command = c.getCommand();
            baudRate = c.getBaudRate();
            bits = c.getBits();
            stopBits = c.getStopBits();
            parity = c.getParity();
        }
    }

}
