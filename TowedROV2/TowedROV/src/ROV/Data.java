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

import jssc.SerialPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import SerialCom.*;

/**
 * This class is used for storing and sharing all variables and resources
 * between the threads
 */
public class Data extends Observable {

    //Internal variables for ROV
    boolean ERROR_I2C = false;
    String dataToSend = "";
    String dataToSerial = "";
    boolean gatheringDataToSend = false;
    int actuatorDifference = 0;
    

    // Calibration values
    int pressureSensorOffset = 0;
    private final static int STEPPER_ANGLEADJUST = 0;

    // Command values
    int cmd_lightMode = 0;
    int cmd_actuatorPS = 0;
    int cmd_actuatorSB = 0;
    int cmd_bothActuators = 0;
//    int cmd_actuatorPSMaxPos = 0;
//    int cmd_actuatorPSMinPos = 0;
//    int cmd_actuatorSBMaxPos = 0;
//    int cmd_actuatorSBMinPos = 0;
    int cmd_BlueLED = 0;

    boolean cmd_disableMotors = true;

    int cmd_pressureAtSeaLevel = 0;

    int cmd_targetMode = 0; // Mode 0 = depth, 1 = seafloor, 2 = manual
    double cmd_targetDistance = 0;

    double cmd_offsetDepthBeneathROV = 0;
    double cmd_offsetROVdepth = 0;

    //int cmd_depth = 0;
//    int cmd_cameraPitch = 0;
//    int cmd_cameraRoll = 0;
//    byte cmd_cameraMode = 0;
    double cmd_pid_p = 0;
    double cmd_pid_i = 0;
    double cmd_pid_d = 0;
    double cmd_pid_gain = 0;

    boolean cmd_emergencySurface = false;
    boolean cmd_ack = false;
//    boolean cmd_manualWingControl = false;

    double cmd_imuCalibrateRoll = 0;
    double cmd_imuCalibratePitch = 0;

    boolean cmd_ping = false;
    boolean clientConnected = false;

    // Sensor values
    boolean fb_ROVReady = false;

    double fb_depthBeneathROV = 12;
    double fb_depthBeneathBoat = 0;
    double cmd_currentROVdepth = 0;

    int fb_speedThroughWather = 0;
    int fb_waterTemperature = 0;

    int fb_stepperPos = 0;

    double fb_tempMainElBoxFront = 0;
    double fb_tempMainElBoxRear = 0;

    int fb_currentDraw = 0;
    double fb_pitchAngle = 0;
    double fb_rollAngle = 0;

    int fb_heading = 0;

    //Input channels
    double analogInputChannel_1 = 0.00;
    double analogInputChannel_2 = 0.00;

    boolean digitalInputChannel_3 = false;
    boolean digitalInputChannel_4 = false;

    //Class variables
    private int counter = 0;
    private boolean i2cRequest = false;
    
    
    //Serial port stepperArduino
    SerialPort serialPort;

    /**
     * A list over availible com ports
     */
    public HashMap<String, String> comPortList = new HashMap<>();

    /**
     * A list over all internal alarms in the ROV
     */
    public ConcurrentHashMap<String, Boolean> completeAlarmListDh = new ConcurrentHashMap<>();

    /**
     * Returns the current ack status
     *
     * @return ack status
     */
    public boolean isCmd_ack() {
        return cmd_ack;
    }

    /**
     * Sets the current ack status
     *
     * @param cmd_ack the current ack status
     */
    public void setCmd_ack(boolean cmd_ack) {
        this.cmd_ack = cmd_ack;
    }

    //Sensor values getters and setters
    /**
     * Returns the depth beneath the ROV in meters
     *
     * @return depth beneath the ROV in meters
     */
    public double getFb_depthBeneathROV() {
        return fb_depthBeneathROV;
    }

    /**
     * Sets the depth beneath the ROV in meters
     *
     * @param fb_depthBeneathROV the depth beneath the ROV in meters
     */
    public void setFb_depthBeneathROV(double fb_depthBeneathROV) {
        this.fb_depthBeneathROV = fb_depthBeneathROV;
    }

    /**
     * Returns the depth below the boat in meters
     *
     * @return the depth below the boat in meters
     */
    public double getFb_depthBeneathBoat() {
        return fb_depthBeneathBoat;
    }

    /**
     * Sets the depth beaneath the boat
     *
     * @param fb_depthBeneathBoat the depth beaneath the boat
     */
    public void setFb_depthBeneathBoat(double fb_depthBeneathBoat) {
        this.fb_depthBeneathBoat = fb_depthBeneathBoat;
    }

    /**
     * Returns the speed through water
     *
     * @return the speed through water
     */
    public int getFb_speedThroughWather() {
        return fb_speedThroughWather;
    }

    /**
     * Sets the speed through water
     *
     * @param fb_speedThroughWather the speed through water
     */
    public void setFb_speedThroughWather(int fb_speedThroughWather) {
        this.fb_speedThroughWather = fb_speedThroughWather;
    }

    /**
     * Returns the water temperature
     *
     * @return the water temperature
     */
    public int getFb_waterTemperature() {
        return fb_waterTemperature;
    }

    /**
     * Sets the water temperature
     *
     * @param fb_waterTemperature the water temperature
     */
    public void setFb_waterTemperature(int fb_waterTemperature) {
        this.fb_waterTemperature = fb_waterTemperature;
    }

    /*
    /**
     * Returns the stepper position
     *
     * @return the stepper position
     *
    public int getFb_stepperPos() {

        return fb_stepperPos;
    }

    /**
     * Sets the stepper position
     *
     * @param fb_stepperPos the stepper position
     *
    public void setFb_stepperPos(int fb_stepperPos) {
//        setChanged();
//        notifyObservers();
        this.fb_stepperPos = fb_stepperPos + STEPPER_ANGLEADJUST;
    }

    /**
     * Returns the SB actuator feedback
     *
     * @return the SB actuator feedback
     *
    public int getFb_actuatorSBPos() {
        return fb_actuatorSBPos;
    }

    /**
     * Sets the SB actuator position
     *
     * @param fb_actuatorSBPos the SB actuator position
     *
    public void setFb_actuatorSBPos(int fb_actuatorSBPos) {
//        setChanged();
//        notifyObservers();
        this.fb_actuatorSBPos = fb_actuatorSBPos + SB_ACTUATOR_ANGLEADJUST;

    }
    */
    
    
    /**
     * Returns the temperature in the front of the main electronic box
     *
     * @return the temperature in the front of the main electronic box
     */
    public double getFb_tempMainElBoxFront() {
        return fb_tempMainElBoxFront;
    }

    /**
     * Sets the temperature in front of the the main electronic box
     *
     * @param fb_tempMainElBoxFront the temperature in front of the in the main
     * electronic box
     */
    public void setFb_tempMainElBoxFront(double fb_tempMainElBoxFront) {
        this.fb_tempMainElBoxFront = fb_tempMainElBoxFront;
    }

    /**
     * Returns the remperature in the rear of the main electronic box
     *
     * @return the remperature in the rear of the main electronic box
     */
    public double getFb_tempMainElBoxRear() {
        return fb_tempMainElBoxRear;
    }

    /**
     * Sets the remperature in the rear of the main electronic box
     *
     * @param fb_tempMainElBoxRear the remperature in the rear of the main
     * electronic box
     */
    public void setFb_tempMainElBoxRear(double fb_tempMainElBoxRear) {
        this.fb_tempMainElBoxRear = fb_tempMainElBoxRear;
    }

    /**
     * Returns the current draw
     *
     * @return the current draw
     */
    public int getFb_currentDraw() {
        return fb_currentDraw;
    }

    /**
     * Sets the current draw
     *
     * @param fb_currentDraw the current draw
     */
    public void setFb_currentDraw(int fb_currentDraw) {
        this.fb_currentDraw = fb_currentDraw;
    }

    /**
     * Returns the pitch andgle of the ROV
     *
     * @return the pitch andgle of the ROV
     */
    public double getFb_pitchAngle() {
        return fb_pitchAngle + getCmd_imuCalibratePitch();
    }

    /**
     * Sets the pitch andgle of the ROV
     *
     * @param fb_pitchAngle the pitch andgle of the ROV
     *
     */
    public void setFb_pitchAngle(double fb_pitchAngle) {
//        setChanged();
//        notifyObservers();
        this.fb_pitchAngle = fb_pitchAngle;
    }

    /**
     * Returns the roll angle of the ROV
     *
     * @return the roll angle of the ROV
     */
    public double getFb_rollAngle() {

        return fb_rollAngle + getCmd_imuCalibrateRoll();
    }

    /**
     * Sets the roll angle of the ROV
     *
     * @param fb_rollAngle the roll angle of the ROV
     */
    public void setFb_rollAngle(double fb_rollAngle) {
//        setChanged();
//        notifyObservers();
        this.fb_rollAngle = fb_rollAngle;
    }

    /**
     * Returns the heading of the ROV
     *
     * @return the heading of the ROV
     */
    public int getFb_heading() {
        return fb_heading;
    }

    /**
     * Sets the heading of the ROV
     *
     * @param fb_heading the heading of the ROV
     */
    public void setFb_heading(int fb_heading) {
        this.fb_heading = fb_heading;
    }

    /**
     * Returns the light mode of the ROV
     *
     * @return the light mode of the ROV
     */
    public int getCmd_lightMode() {
        return cmd_lightMode;
    }

    /**
     * Sets the light mode of the ROV
     *
     * @param cmd_lightMode the light mode of the ROV
     */
    public void setCmd_lightMode(int cmd_lightMode) {
        this.cmd_lightMode = this.cmd_lightMode;
    }

    /**
     * Return the PS actuator target command
     *
     * @return the PS actuator target command
     */
    public int getCmd_actuatorPS() {
        return cmd_actuatorPS;
    }

    /**
     * Sets the PS actuator target command and notifiy the observers
     *
     * @param cmd_actuatorPS the PS actuator target command
     */
    public void setCmd_actuatorPS(int cmd_actuatorPS) {
        this.cmd_actuatorPS = cmd_actuatorPS;
        setChanged();
        notifyObservers();

    }

    /**
     * Return the SB actuator target command
     *
     * @return the SB actuator target command
     */
    public int getCmd_actuatorSB() {
        return cmd_actuatorSB;
    }

    /**
     * Sets the SB actuator target command and notifiy the observers
     *
     * @param cmd_actuatorSB the SB actuator target command and notifiy the
     * observers
     */
    public void setCmd_actuatorSB(int cmd_actuatorSB) {
        this.cmd_actuatorSB = cmd_actuatorSB;
        setChanged();
        notifyObservers();
    }

    /**
     * Return the command target mode
     *
     * @return the command target mode
     */
    public int getcmd_targetMode() {
        return cmd_targetMode;
    }

    /**
     * Sets the command target mode and notifiy the observers
     *
     * @param cmd_targetMode the command target mode and notifiy the observers
     */
    public void setcmd_targetMode(int cmd_targetMode) {
        this.cmd_targetMode = cmd_targetMode;
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the command value for the PID P value
     *
     * @return the command value for the PID P value
     */
    public double getCmd_pid_p() {
        return cmd_pid_p;
    }

    /**
     * Sets the command value for the PID P value
     *
     * @param cmd_pid_p the command value for the PID P value
     */
    public void setCmd_pid_p(double cmd_pid_p) {
        this.cmd_pid_p = cmd_pid_p;
    }

    /**
     * Return the command value for the PID I value
     *
     * @return the command value for the PID I value
     */
    public double getCmd_pid_i() {
        return cmd_pid_i;
    }

    /**
     * Sets the command value for the PID I value
     *
     * @param cmd_pid_i the command value for the PID I value
     */
    public void setCmd_pid_i(double cmd_pid_i) {
        this.cmd_pid_i = cmd_pid_i;
    }

    /**
     * Sets the command value for the PID D value
     *
     * @return the command value for the PID D value
     */
    public double getCmd_pid_d() {
        return cmd_pid_d;
    }

    /**
     * Sets the command value for the PID D value
     *
     * @param cmd_pid_d the command value for the PID D value
     */
    public void setCmd_pid_d(double cmd_pid_d) {
        this.cmd_pid_d = cmd_pid_d;
    }

    /**
     * Return the command value for the PID gain value
     *
     * @return the command value for the PID gain value
     */
    public double getCmd_pid_gain() {
        return cmd_pid_gain;
    }

    /**
     * Sets the command value for the PID gain value
     *
     * @param cmd_pid_gain the command value for the PID gain value
     */
    public void setCmd_pid_gain(double cmd_pid_gain) {
        this.cmd_pid_gain = cmd_pid_gain;
    }

    /**
     * Return the emergency surface value of the ROV
     *
     * @return the emergency surface value of the ROV
     */
    public boolean isCmd_emergencySurface() {
        return cmd_emergencySurface;
    }

    /**
     * Sets the emergency surface value of the ROV
     *
     * @param cmd_emergencySurface the emergency surface value of the ROV
     */
    public void setCmd_emergencySurface(boolean cmd_emergencySurface) {
        this.cmd_emergencySurface = cmd_emergencySurface;
    }

    /**
     * Return the ROV current depth
     *
     * @return the ROV current depth
     */
    public double getCmd_currentROVdepth() {
        return cmd_currentROVdepth;
    }

    /**
     * Sets the ROV current depth
     *
     * @param cmd_currentROVdepth the ROV current depth
     */
    public void setCmd_currentROVdepth(double cmd_currentROVdepth) {
        this.cmd_currentROVdepth = cmd_currentROVdepth + cmd_offsetDepthBeneathROV;
        setChanged();
        notifyObservers();

    }

    /**
     * Returns the counter value
     *
     * @return the counter value
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter value
     *
     * @param counter the counter value
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * Return the value of analog input channel 1
     *
     * @return the value of analog input channel 1
     */
    public double getAnalogInputChannel_1() {
        return analogInputChannel_1;
    }

    /**
     * Sets the value of analog input channel 1
     *
     * @param analogInputChannel_1 the value of analog input channel 1
     */
    public void setAnalogInputChannel_1(double analogInputChannel_1) {
        this.analogInputChannel_1 = analogInputChannel_1;
    }

    /**
     *
     * Return the value of analog input channel 2
     *
     * @return the value of analog input channel 2
     */
    public double getAnalogInputChannel_2() {
        return analogInputChannel_2;
    }

    /**
     * Sets the value of analog input channel 2
     *
     * @param analogInputChannel_2 the value of analog input channel 2
     */
    public void setAnalogInputChannel_2(double analogInputChannel_2) {
        this.analogInputChannel_2 = analogInputChannel_2;
    }

    /**
     * Return the value of digital input 3
     *
     * @return the value of digital input 3
     */
    public boolean isDigitalInputChannel_3() {
        return digitalInputChannel_3;
    }

    /**
     * Sets the value of digital input 3
     *
     * @param digitalInputChannel_3 the value of digital input 3
     */
    public void setDigitalInputChannel_3(boolean digitalInputChannel_3) {
        this.digitalInputChannel_3 = digitalInputChannel_3;
    }

    /**
     * Return the value of digital input 4
     *
     * @return the value of digital input 4
     */
    public boolean isDigitalInputChannel_4() {
        return digitalInputChannel_4;
    }

    /**
     * Sets the value of digital input 4
     *
     * @param digitalInputChannel_4 the value of digital input 4
     */
    public void setDigitalInputChannel_4(boolean digitalInputChannel_4) {
        this.digitalInputChannel_4 = digitalInputChannel_4;
    }

    /**
     * Return the IMU Roll calobratio value
     *
     * @return the IMU Roll calobratio value
     */
    public double getCmd_imuCalibrateRoll() {
        return cmd_imuCalibrateRoll;
    }

    /**
     * Sets the IMU Roll calobratio value
     *
     * @param cmd_imuCalibrateRoll the IMU Roll calobratio value
     */
    public void setCmd_imuCalibrateRoll(double cmd_imuCalibrateRoll) {
        this.cmd_imuCalibrateRoll = cmd_imuCalibrateRoll;
    }

    /**
     * Return the IMU Pitch calobratio value
     *
     * @return the IMU Pitch calobratio value
     */
    public double getCmd_imuCalibratePitch() {
        return cmd_imuCalibratePitch;
    }

    /**
     * Sets the IMU Pitch calobratio value
     *
     * @param cmd_imuCalibratePitch the IMU Pitch calobratio value
     */
    public void setCmd_imuCalibratePitch(double cmd_imuCalibratePitch) {
        this.cmd_imuCalibratePitch = cmd_imuCalibratePitch;
    }

    /**
     * Returns the command value for the blue LED
     *
     * @return the command value for the blue LED
     */
    public int getCmd_BlueLED() {
        return cmd_BlueLED;
    }

    /**
     * Sets the command value for the blue LED
     *
     * @param cmd_BlueLED the command value for the blue LED and notify the
     * observers
     */
    public void setCmd_BlueLED(int cmd_BlueLED) {
        this.cmd_BlueLED = cmd_BlueLED;
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the ROV ready value
     *
     * @return the ROV ready value
     */
    public boolean getFb_ROVReady() {
//        setChanged();
//        notifyObservers();
        return fb_ROVReady;
    }

    /**
     * Sets the ROV ready value
     *
     * @param fb_ROVReady the ROV ready value
     */
    public void setFb_ROVReady(boolean fb_ROVReady) {
        this.fb_ROVReady = fb_ROVReady;
    }

    /**
     * Return the I2C error value
     *
     * @return the I2C error value
     */
    public boolean getERROR_I2C() {
        return ERROR_I2C;
    }

    /**
     * Sets the I2C error value
     *
     * @param ERROR_I2C the I2C error value
     */
    public void setERROR_I2C(boolean ERROR_I2C) {
        this.ERROR_I2C = ERROR_I2C;
    }


    /**
     * Returns the command value for both the actuators
     *
     * @return the command value for both the actuators
     */
    public int getCmd_bothActuators() {
        return cmd_bothActuators;
    }

    /**
     * Sets the command value for both the actuators
     *
     * @param cmd_bothActuators the command value for both the actuators and
     * notify the observers
     */
    public void setCmd_bothActuators(int cmd_bothActuators) {
        this.cmd_bothActuators = cmd_bothActuators;
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the actuator difference
     *
     * @return the actuator difference
     */
    public int getActuatorDifference() {
        return actuatorDifference;
    }

    /**
     * Sets the actuator difference
     *
     * @param actuatorDifference the actuator difference
     */
    public void setActuatorDifference(int actuatorDifference) {
        this.actuatorDifference = actuatorDifference;
    }

    /**
     * Returns the target distance command
     *
     * @return the target distance command
     */
    public double getCmd_targetDistance() {
        return cmd_targetDistance;
    }

    /**
     * Sets the target distance command
     *
     * @param cmd_targetDistance the target distance command
     */
    public void setCmd_targetDistance(double cmd_targetDistance) {
        this.cmd_targetDistance = cmd_targetDistance;
    }

    /**
     * Returns the disable motor controllers command
     *
     * @return the disable motor controllers command
     */
    public boolean getCmd_disableMotors() {
        return cmd_disableMotors;
    }

    /**
     * Sets the disable motor controllers command
     *
     * @param cmd_disableMotors the disable motor controllers command
     */
    public void setCmd_disableMotors(boolean cmd_disableMotors) {
        this.cmd_disableMotors = cmd_disableMotors;
    }

    /**
     * Returns the offset depth beneath the ROV
     *
     * @return the offset depth beneath the ROV
     */
    public double getCmd_offsetDepthBeneathROV() {
        return cmd_offsetDepthBeneathROV;
    }

    /**
     * Sets the offset depth beneath the ROV
     *
     * @param cmd_offsetDepthBeneathROV the offset depth beneath the ROV
     */
    public void setCmd_offsetDepthBeneathROV(double cmd_offsetDepthBeneathROV) {
        this.cmd_offsetDepthBeneathROV = cmd_offsetDepthBeneathROV;
    }

    /**
     * Returns the offset depth of the ROV
     *
     * @return the offset depth of the ROV
     */
    public double getCmd_offsetROVdepth() {
        return cmd_offsetROVdepth;
    }

    /**
     * Sets the offset depth of the ROV
     *
     * @param cmd_offsetROVdepth the offset depth of the ROV
     */
    public void setCmd_offsetROVdepth(double cmd_offsetROVdepth) {
        this.cmd_offsetROVdepth = cmd_offsetROVdepth;
    }

    /**
     * Returns the ping value
     *
     * @return the ping value
     */
    public boolean isCmd_ping() {
        return cmd_ping;
    }

    /**
     * Sets the ping value
     *
     * @param cmd_ping the ping value
     */
    public void setCmd_ping(boolean cmd_ping) {
        this.cmd_ping = cmd_ping;
    }

    /*'
     * Returns  the client connected status
     * @return the client connected status
     */
    public boolean isClientConnected() {
        return clientConnected;
    }

    /**
     * Sets the client connected status
     *
     * @param clientConnected the client connected status
     */
    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }
    
    /**
     * Sets the serialport for stepperArduino connection
     * @param serialPort the serialport to be stored
     */
    public void setSerialPortStepper(SerialPort serialPort){
        System.out.println("Serialport for stepperarduino is set in data");
        this.serialPort = serialPort;
    }
    
    /**
     * returns the serialport of the stepperArduino
     * @return the serialport of the stepperArduino
     */
    public SerialPort getSerialPort(){
        System.out.println("getserialport() is called in data");
        return serialPort;
    }
     /**
     * Returns the stepper position
     *
     * @return the stepper position
     */
    public int getFb_stepperPos() {

        return fb_stepperPos;
    }

    /**
     * Sets the stepper position
     *
     * @param fb_stepperPos the PS actuator position
     */
    public void setFb_stepperPos(int fb_stepperPos) {
//        setChanged();
//        notifyObservers();
        this.fb_stepperPos = fb_stepperPos + STEPPER_ANGLEADJUST;
    }
    
     /**
     * Returns the dataToSerial
     *
     * @return the dataToSerial
     */
    public String getDataToSerial() {
        return dataToSerial;
    }

    /**
     * Sets the dataToSerial
     *
     * @param dataToSerial the dataToSend
     */
    public void setDataToSerial(String dataToSerial) {
        this.dataToSerial = dataToSerial;
        setChanged();
        notifyObservers();
    }
    
    
    /**
     * Returns the dataToSend
     *
     * @return the dataToSend
     */
    public String getDataToSend() {
        return dataToSend;
    }

    /**
     * Sets the dataToSend
     *
     * @param dataToSend the dataToSend
     */
    public void setDataToSend(String dataToSend) {
        this.dataToSend = dataToSend;
    }
    

    /**
     * Returns the status of gatheringDataToSend
     *
     * @return the status of gatheringDataToSend
     */
    public boolean isGatheringDataToSend() {
        return gatheringDataToSend;
    }

    /**
     * Sets the status of gatheringDataToSend
     *
     * @param gatheringDataToSend the status of gatheringDataToSend and notify
     * the observers
     */
    public void setGatheringDataToSend(boolean gatheringDataToSend) {
        this.gatheringDataToSend = gatheringDataToSend;
        setChanged();
        notifyObservers();
    }

}
