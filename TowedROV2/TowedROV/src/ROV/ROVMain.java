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
package ROV;

import ROV.*;
import I2CCom.*;
import SerialCom.*;
import ROV.TCPCom.Server;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jssc.SerialPortList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The main class of the ROV. It is responsible for starting up the ROVs
 * threads.
 *
 * @author Robin S. Thorholm
 */
public class ROVMain {

    private final static int serverPort = 8080;
    private static Thread serialRW;
    private static Thread I2CComHandler;

    static boolean dataIsRecieved = false;
    static boolean testIsDone = false;
    private static Thread I2CRW;

    /**
     *
     */
    protected static Data dh;

    private static Thread Server;
    private static Thread alarmHandler;

    private static Thread imuThread;
    private static Thread ArduinoIOThread;
    private static Thread ArduinoActuatorFBThread;
    private static Thread StepperArduinoThread;
    private static Thread StepperArduinoWriterThread;

    /**
     * The main method of th ROV. Starts up the threads and instnaciates
     * necessary tasks.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean foundComPort = false;

        ScheduledExecutorService executor
                = Executors.newScheduledThreadPool(8);

        String osName = System.getProperty("os.name");

        String[] portNames = SerialPortList.getPortNames();
        for (int i = 0; i < portNames.length; i++) {
            System.out.println(portNames[i]);
        }
        if (!osName.contains("Windows")) {

        } else {
            System.out.println("OS is windows, does not start raspberry libraries");
        }
        dh = new Data();

        I2CRW I2CRW_this = new I2CRW(dh);
        Logic logic = new Logic(dh, I2CRW_this);
        PID pid = new PID(dh);
        dh.addObserver(logic);

        SerialDataHandler sdh = new SerialDataHandler(dh);

        executor.scheduleAtFixedRate(I2CRW_this,
                20, 40, TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(logic,
                10, 5, TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(pid,
                20, 10, TimeUnit.MILLISECONDS);

        Server = new Thread(new Server(serverPort, dh));
        Server.start();
        Server.setName("Server");
        int inputData = 0;
        if (!foundComPort) {
            System.out.println("Searching for com ports...");
            sdh.findComPorts();
            foundComPort = true;
        }

        for (Map.Entry e : dh.comPortList.entrySet()) {
            String comPortKey = (String) e.getKey();
            String comPortValue = (String) e.getValue();
            if (comPortValue.contains("IMU")) {
                System.out.println("IMUmain");
                imuThread = new Thread(new ReadSerialData(dh, comPortKey, 115200, comPortValue));
                imuThread.start();
                imuThread.setName(comPortValue);
                System.out.println("IMU found");

            }

            if (comPortValue.contains("EchoSounder")) {
                System.out.println("EchoSounderMain");
                ArduinoIOThread = new Thread(new ReadSerialData(dh, comPortKey, 4800, comPortValue));
                ArduinoIOThread.start();
                ArduinoIOThread.setName(comPortValue);
                System.out.println("EchoSounder found");

            }

            if (comPortValue.contains("ActuatorFBArduino")) {
                System.out.println("ActuatorArduinoMain");
                ArduinoActuatorFBThread = new Thread(new ReadSerialData(dh, comPortKey, 38400, comPortValue));
                ArduinoActuatorFBThread.start();
                ArduinoActuatorFBThread.setName(comPortValue);
                System.out.println("ActuatorFBArduino found");

            }
            if (comPortValue.contains("StepperArduino")) {
                System.out.println("StepperArduinoMain");
                StepperArduinoThread = new Thread(new ReadSerialData(dh, comPortKey, 57600, comPortValue));
                StepperArduinoThread.start();
                StepperArduinoThread.setName(comPortValue);
                StepperArduinoWriterThread = new Thread(new WriteSerialData(dh));
                StepperArduinoWriterThread.start();
                System.out.println("StepperArduino found");
            }
        }
        System.out.println("Done");

        dh.setFb_ROVReady(true);
        try {
            dh.setCmd_BlueLED(1);
            Thread.sleep(500);
            dh.setCmd_BlueLED(0);
        } catch (Exception e) {
        }

        while (true) {
            try {

            } catch (Exception e) {
            }
        }

    }
}
