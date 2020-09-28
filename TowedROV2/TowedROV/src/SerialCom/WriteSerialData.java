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

import jssc.SerialPort;
import jssc.SerialPortException;
import java.util.Scanner;
import ROV.Data;


public class WriteSerialData implements Runnable {

    SerialPort serialPort;
    Scanner userInput = new Scanner(System.in);
    Data data = null;
    String comPort = "";
    int baudRate = 0;
    Thread sw;
    String lastMessage = "";
    
    /**
     * constructor for this class. creates a instance of this class and updating
     * the serial port and data class
     *
     * @param data - serial port the class should write to
     * @param baudRate
     */
    public WriteSerialData(Data data) {
        this.data = data;
        serialPort = new SerialPort(comPort);
    }

    @Override
    public void run() {
        while(!serialPort.isOpened()){
            System.out.println("Trying to open port");
        this.serialPort = data.getSerialPort();
        }
        while (serialPort.isOpened()) {
            String dataToSerial = data.getDataToSerial();
            writeToSerial(dataToSerial);
        }

    }//end run

    /**
     * Makes a string containing the values that are to be sent. sends the
     * string to the serial port.
     *
     * @param message the message to be sent.
     */
    public void writeToSerial(String message) {
        //Creating string to send
        
        String outputString = "'" + message + "'";
        //Writes string to serial port
        try{
            if(!outputString.equals("''") && !this.lastMessage.equals(message)) {
            serialPort.writeString(outputString);
            System.out.println(outputString);
                    }
        } catch (SerialPortException e) {
            System.out.println(e);
        }
        this.lastMessage = message;
    }//end writeToSerial
}//end class