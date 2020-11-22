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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handels the actuator logic of the ROV and any other tasks that has
 * a crucial update rate
 *
 * @author Towed ROV 2019
 * https://ntnuopen.ntnu.no/ntnu-xmlui/handle/11250/2564356 edited 2020 removed
 * i2c feedback for actuators
 */
public class Logic implements Runnable {

    Data data = null;

    int old_cmd_BlueLED = 0;

    double elapsedTimer = 0;
    double elapsedTimerNano = 0;
    long lastTime = 0;

    double elapsedTimer_sendData = 0;
    double elapsedTimerNano_sendData = 0;
    long lastTime_sendData = 0;

    private HashMap<String, String> newDataToSend = new HashMap<>();

    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput BlueLED_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "BlueLED", PinState.LOW);

    /**
     *
     * @param data the shared recource data class
     */
    public Logic(Data data) {
        this.data = data;

    }

    /**
     * Crucial tasks is runned in this method:
     *
     * The run method checks if the current actuator position against the given
     * command value. If they are equal an stop command wil bes sendt.
     *
     * It also checks if the communication to the GUI has been lost for more
     * than five seconds
     *
     * Updates the gatherFbData list so it is ready to be sent to the GUI
     */
    @Override
    public void run() {
        try {
            if (data.isCmd_ping()) {
                data.setClientConnected(true);
                elapsedTimer = 0;
                data.setCmd_ping(false);
            }
            elapsedTimerNano = (System.nanoTime() - lastTime);
            elapsedTimer = elapsedTimerNano / 1000000;
            if (elapsedTimer > 5000 && data.isClientConnected()) {
                //System.out.println("Lost connection go to emergency");
            }

            elapsedTimerNano_sendData = (System.nanoTime() - lastTime_sendData);
            elapsedTimer_sendData = elapsedTimerNano_sendData / 1000000;
            if (elapsedTimerNano_sendData > 50) {
                gatherFbData();
                lastTime_sendData = 0;
            }

        } catch (Exception e) {
        }

    }

    /**
     * This method is responsible to update the newDataToSend list so the data
     * is ready to be sendt to the GUI
     */
    public void gatherFbData() {

        newDataToSend.put("Fb_stepperPSPos", String.valueOf(data.getFb_stepperPos()));
        newDataToSend.put("Fb_stepperSBPos", String.valueOf(data.getFb_stepperPos()));
        newDataToSend.put("Fb_rollAngle", String.valueOf(data.getFb_rollAngle()));
        newDataToSend.put("Fb_pitchAngle", String.valueOf(data.getFb_pitchAngle()));
        newDataToSend.put("Fb_depthBeneathROV", String.valueOf(data.getCmd_currentROVdepth()));
        newDataToSend.put("Fb_tempElBoxFront", String.valueOf(data.getFb_tempMainElBoxFront()));
        newDataToSend.put("Fb_tempElBoxRear", String.valueOf(data.getFb_tempMainElBoxRear()));
        newDataToSend.put("Fb_ROVReady", String.valueOf(data.getFb_ROVReady()));
        newDataToSend.put("ERROR_I2C", String.valueOf(data.ERROR_I2C));

        String dataToSend = "<";
        for (Map.Entry e : newDataToSend.entrySet()) {
            String key = (String) e.getKey();
            String value = (String) e.getValue();
            dataToSend = dataToSend + key + ":" + value + ":";
        }
        dataToSend = dataToSend.substring(0, dataToSend.length() - 1);
        dataToSend = dataToSend + ">";

        data.setDataToSend(dataToSend);

    }
}
