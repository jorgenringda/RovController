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
package SerialCom;

import java.util.HashMap;
import java.util.Map;
import jssc.SerialPort;
import jssc.SerialPortException;
import ROV.Data;

/**
 * This class is eespnsible for reading serial data from the IMU, echo sounder
 * and the Arduino I/O
 *@author Towed ROV 2019 https://ntnuopen.ntnu.no/ntnu-xmlui/handle/11250/2564356
 * edited 2020, added feedback from stepperPos to the switch case
 *
 */
public class ReadSerialData implements Runnable {

    // Filter values
    int stepperFbFilter = 1; // 1 equals off

    int stepperFbFilterStorage = 0;

    int stepperFbFilterCounter = 0;
    
    boolean portIsOpen = false;
    String comPort = "";
    String myName = "";
    int baudRate = 0;
    Data data = null;
    SerialPort serialPort;

    /**
     * The complete list of alla incomming data and its values
     */
    public HashMap<String, String> incommingData = new HashMap<>();

    /**
     *
     * @param data the shared recource data class
     * @param comPort the com port it should use
     * @param baudRate the baud rate of the com port
     * @param myName the name of the com device it should connect to
     */
    public ReadSerialData(Data data, String comPort, int baudRate, String myName) {
        this.comPort = comPort;
        this.myName = myName;
        this.baudRate = baudRate;
        this.data = data;
    }

    /**
     * Run command loops through the readData
     */
    @Override
    public void run() {
        while (true) {
            try {

                readData(comPort, baudRate);
            } catch (Exception e) {
            }

        }
    }

    /**
     * readData is responsible for gathering data from the serial devices
     *
     * @param comPort the com port it should connect to
     * @param baudRate the boud rate of the com port
     */
    public void readData(String comPort, int baudRate) {

        boolean recievedData = false;
        //Declare special symbol used in serial data stream from Arduino
        String startChar = "<";
        String endChar = ">";
        String seperationChar = ":";

        serialPort = new SerialPort(comPort);

        if (!portIsOpen) {
            try {
                serialPort.openPort();
                portIsOpen = true;
                if (myName.contains("StepperArduino")){
                data.setSerialPortStepper(serialPort);
            }
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }

        while (recievedData == false) {
            try {
                Thread.sleep(50);
            } catch (Exception ex) {

            }
            String buffer;

            try {
                serialPort.setParams(baudRate, 8, 1, 0);
                buffer = serialPort.readString();

                // System.out.println(buffer);
                boolean dataNotNull = false;
                boolean dataHasFormat = false;

                if ((buffer != null)) {
                    dataHasFormat = true;
                } else {
                    dataHasFormat = false;
                    dataNotNull = false;

                }

                if (dataHasFormat) {
                    String dataStream = buffer;

                    dataStream = dataStream.substring(dataStream.indexOf(startChar) + 1);
                    dataStream = dataStream.substring(0, dataStream.indexOf(endChar));
                    dataStream = dataStream.replace("?", "");
                    String[] data = dataStream.split(seperationChar);

                    for (int i = 0; i < data.length; i = i + 2) {
                        //this.data.data.put(data[i], data[i + 1]);
                        incommingData.put(data[i], data[i + 1]);

                    }

                    sendIncommingDataToDataHandler();
                }

//            if (elapsedTimer != 0)
//            {
//                
//                System.out.println("Data is recieved in: " + elapsedTimer + " millis"
//                        + " or with: " + 1000 / elapsedTimer + " Hz");
//            } else
//            {
//                System.out.println("Data is recieved in: " + elapsedTimer + " millis"
//                        + " or with: unlimited Hz!");
//            }
            } catch (Exception ex) {
                // System.out.println("Lost connection to " + myName);
            }

        }
    }

    private void sendIncommingDataToDataHandler() {
        for (Map.Entry e : incommingData.entrySet()) {
            String key = (String) e.getKey();
            String value = (String) e.getValue();

            switch (key) {
                case "fb_stepperPos":
                    int positionValue = Integer.parseInt(value);
                        data.setFb_stepperPos(positionValue);
                    break;
                    
                    
                case "D":
                    double doubleValue = Double.parseDouble(value) * -1;
                    data.setFb_depthBeneathROV(doubleValue);
                    break;
                    
//                case "DBT":
//                    data.setFb_depthBelowTransduser(Double.parseDouble(value));
//                    break;
                    
                case "ch1":
                    data.setAnalogInputChannel_1(Double.parseDouble(value));
                    break;
                case "ch2":
                    data.setAnalogInputChannel_2(Double.parseDouble(value));
                    break;

                case "ch3":
                    if (value.equals("1.00")) {
                        data.setDigitalInputChannel_3(true);
                    } else {
                        data.setDigitalInputChannel_3(false);
                    }
                    break;
                case "ch4":
                    if (value.equals("1.00")) {
                        data.setDigitalInputChannel_4(true);
                    } else {
                        data.setDigitalInputChannel_4(false);
                    }
                    break;
                    
                case "Roll":
                    data.setFb_rollAngle(Double.parseDouble(value));
                    //setRoll(Integer.parseInt(value));
                    break;
                case "Pitch":
                    data.setFb_pitchAngle(Double.parseDouble(value));
                    //setPitch(Integer.parseInt(value));
                    break;
                case "Heading":
                    data.setFb_heading(Integer.parseInt(value));
                    //setHeading(Integer.parseInt(value));
                    break;

                case "tmp1":
                    data.setFb_tempMainElBoxFront(Double.parseDouble(value));
                    break;

                case "tmp2":
                    data.setFb_tempMainElBoxRear(Double.parseDouble(value));
                    break;
            }
        }
    }
    }



