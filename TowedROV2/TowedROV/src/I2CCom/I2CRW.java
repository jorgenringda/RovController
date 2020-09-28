/*
 * This code is for the bachelor thesis named "Towed-ROV".
 * The purpose is to build a ROV which will be towed behind a surface vessel
 * and act as a multi-sensor platform, were it shall be easy to place new 
 * sensors. There will also be a video stream from the ROV.
 * 
 * The system consists of two Raspberry Pis in the ROV that is connected to
 * several Arduino micro controllers. These micro controllers are connected to
 * feedback from the actuators, the echo sounder and extra optional sensors.
 * The external computer which is on the surface vessel is connected to a GPS,
 * echo sounder over USB, and the ROV over ethernet. It will present and
 * log data in addition to handle user commands for controlling the ROV.
 */
package I2CCom;

import ROV.Data;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * This class is responsible for sending and reciving data from and to I2C
 * devices.
 */
public class I2CRW implements Runnable {

    protected static Data data;

    I2CDevice arduinoIO;
    I2CDevice actuatorSB;
    I2CDevice actuatorPS;

    //User settings
    //SB and PS 180 target equals 0 degrees wing pos
    private final static int PS_ACTUATOR_SPEED = 50;
    private final static int SB_ACTUATOR_SPEED = 50;

    //Polulu JRK drive commands 
    private final static int PS_ACTUATOR_ADDRESS = 0x10;
    private final static int SB_ACTUATOR_ADDRESS = 0x0F;
    private final static int ACTUATOR_STOP = 0xFF;

    //Arduino Address
    private final static int ARDUINO_IO_ADDRESS = 0x0B;

    //JRK commands 
    int JRK_setTargetLowResRev = 0xE0;
    int JRK_setTargetLowResFwd = 0xE1;
    int JRK_getScaledFeedback = 0xA7; //The low byte of “Feedback”

    String start_char = "<";
    String end_char = ">";
    String sep_char = ":";

    /**
     * Constructor of the i2CRW class Initiates the bus and adds slaves
     *
     * @param data the shared recource data class
     */
    public I2CRW(Data data) {
        this.data = data;

        try {
            //System.out.println("Creatingbus");
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_3);
            //System.out.println("Creatingdevices");
            arduinoIO = bus.getDevice(ARDUINO_IO_ADDRESS);
            actuatorSB = bus.getDevice(SB_ACTUATOR_ADDRESS);
            actuatorPS = bus.getDevice(PS_ACTUATOR_ADDRESS);

        } catch (Exception e) {
            System.out.println("Failed to instansiate I2 Bus");
        }

    }

    /**
     * The run method does nothing in this class
     */
    @Override
    public void run() {
        while (true) {

        }
    }

    /**
     * This method is responsible for sending data to an I2C device
     *
     * @param device the device the data should be sent to
     * @param commandValue the command value that should be sent
     */
    public void sendI2CData(String device, int commandValue) {
        if (!data.getCmd_disableMotors()) {
            try {
                switch (device) {
                    case "ActuatorPS_setTarget":

                        if (commandValue > data.getFb_stepperPos()
                                && commandValue > 0
                                && commandValue <= 254) {
                            actuatorPS.write(JRK_setTargetLowResFwd, (byte) PS_ACTUATOR_SPEED);
                        }

                      /*  if (commandValue < data.getFb_actuatorPSPos()
                                && commandValue > 0
                                && commandValue <= 254) {
                            actuatorPS.write(JRK_setTargetLowResRev, (byte) PS_ACTUATOR_SPEED);
                        } */
                        if (commandValue == 0) {
                            actuatorPS.write(JRK_setTargetLowResRev, (byte) 0);
                        }
                        break;
                    case "ActuatorSB_setTarget":

                        if (commandValue > data.getFb_stepperPos()
                                && commandValue > 0
                                && commandValue <= 254) {
                            actuatorSB.write(JRK_setTargetLowResFwd, (byte) SB_ACTUATOR_SPEED);
                        }

                       /* if (commandValue < data.getFb_actuatorSBPos()
                                && commandValue > 0
                                && commandValue <= 254) {
                            actuatorSB.write(JRK_setTargetLowResRev, (byte) SB_ACTUATOR_SPEED);
                        }*/

                        if (commandValue == 0) {
                            actuatorSB.write(JRK_setTargetLowResRev, (byte) 0);
                        }
                        break;
                    case "ActuatorSB_stopMotor":
                        //actuatorSB.write((byte) ACTUATOR_STOP);
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }

                        actuatorSB.write(JRK_setTargetLowResRev, (byte) 0);
                        break;
                    case "ActuatorPS_stopMotor":
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                        //actuatorPS.write((byte) ACTUATOR_STOP);
                        actuatorPS.write(JRK_setTargetLowResRev, (byte) 0);
                        break;

                }
            } catch (Exception e) {
                data.setERROR_I2C(true);
                System.out.println("Error at I2C read write");
                System.out.println(e);
                //Error writing to i2c
            }
        }
    }

    /**
     * Reads I2C data from an device
     *
     * Not used!
     *
     * @param device the device it should gather data from
     */
    public void readI2CData(String device) {
        byte[] inputDataRaw = new byte[32];

        String dataRecieved = "";
        try {
            switch (device) {

                case "Stepper_Feedback":

                    // int test = actuatorSB.read(JRK_getScaledFeedback);
                    byte[] byteArraySB = new byte[2];
                    actuatorSB.read(0xA7, byteArraySB, 0, 2);
                    int posSB = byteArraySB[0] + 256 * byteArraySB[1];

//                    int posSB = actuatorSB.read(JRK_getScaledFeedback);
                    data.setFb_stepperPos(posSB);
                    break;

                /* case "ActuatorPS_Feedback":
                    byte[] byteArrayPS = new byte[2];
                    actuatorPS.read(0xA7, byteArrayPS, 0, 2);
                    int posPS = byteArrayPS[0] + 256 * byteArrayPS[1];
                    data.setFb_actuatorPSPos(posPS);
                    break; */

                case "ArduinoIO":
                    byte[] buffer = new byte[20];
                    arduinoIO.read(buffer, 0, 20);
                    String bufferData = new String(buffer);
                    //arduinoIO.read(inputDataRaw, 0, 6);
                    int sizeOfRecievedData = 0;

                    for (byte b : inputDataRaw) {
                        if (b != -1) {
                            sizeOfRecievedData++;
                        }
                    }

                    byte[] inputData = new byte[sizeOfRecievedData];
                    System.arraycopy(inputDataRaw, 0, inputData, 0, sizeOfRecievedData);

                    dataRecieved = new String(inputData);

                    break;

            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
