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

import java.util.Observable;
import java.util.Observer;
import com.stormbots.MiniPID;
import java.util.concurrent.atomic.*;

/**
 * This class is responisble for the PID controller and its output to the
 * actuators
 *
 */
public class PID implements Runnable, Observer {

    Data data = null;
    MiniPID miniPID;

    AtomicInteger atomicTarget = new AtomicInteger(0);
    AtomicInteger atomicActual = new AtomicInteger(0);

    double target = 0;
    double actual = 0;
    Double output = new Double(0);

    /**
     * The constructor of the PID class. Creates a miniPID instance.
     *
     * @param data the shared recource data class
     */
    public PID(Data data) {
        this.data = data;
        miniPID = new MiniPID(data.getCmd_pid_p(), data.getCmd_pid_i(), data.getCmd_pid_d());
        miniPID.setOutputLimits(0, 2000);

    }

    /**
     * Run methods gathers the input data and calculates an output value
     */
    @Override
    public void run() {

        if (data.getcmd_targetMode() != 2) {
            if (data.getcmd_targetMode() == 0) {
                //Goal: Get to desired depth
                actual = data.getCmd_currentROVdepth();
            }
            if (data.getcmd_targetMode() == 1) {
                //Goal: Get to desired elevation above seafloor
                actual = data.getFb_depthBeneathROV();
            }

            target = data.getCmd_targetDistance();

            miniPID.setSetpoint(target);
            output = miniPID.getOutput(actual, target);

            data.setDataToSerial(output.toString());
        }
    }

    /**
     *
     * @param o the observer
     * @param arg the arguments for the observer
     */
    @Override
    public void update(Observable o, Object arg) {
        
//        target = data.getCmd_setDepth();
//        actual = data.getFb_depthFromPressure();

//        atomicTarget.set(data.getCmd_setDepth());
//        atomicTarget.set(data.getCmd_bothActuators());
//
//        atomicActual.set((data.getFb_actuatorPSPos() + data.getFb_actuatorPSPos()) / 2);
//        data.setActuatorDifference(data.getFb_actuatorPSPos() - data.getFb_actuatorSBPos());
    }

}
