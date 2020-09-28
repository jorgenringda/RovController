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

/**
 * Startup calibration of the ROV. Used for monitoring the speed of wings on the
 * rov incase of performance loss between startups.
 *
 * Not yet implemented
 */
public class StartupCalibration {

    Data dh = null;
    long currentTime = 0;
    long lastTimePS = 0;
    long lastTimeSB = 0;

    public StartupCalibration(Data dh) {
        this.dh = dh;
    }

    // Startup calibration not yet implemented
//    public String doStartupCalibration()
//    {
//        calibrateActuators();
//        testLights();
//
//        return "Calibration complete...";
//    }
//
//    public void calibrateActuators()
//    {
//        int accuracy = 4;
//        int lastActuatorPSPos = 0;
//        int lastActuatorSBPos = 0;
//        boolean findingMinPS = true;
//        boolean findingMinSB = true;
//        boolean findingMaxPS = true;
//        boolean findingMaxSB = true;
//
//        currentTime = System.nanoTime();
//
//        //Move actuators to minimum pos;
//        dh.cmd_actuatorPS = 0;
//        dh.cmd_actuatorSB = 0;
//        while (findingMinPS && findingMinSB)
//
//        {
//            lastActuatorPSPos = dh.fb_actuatorPSPos;
//            if (lastActuatorPSPos - accuracy <= dh.fb_actuatorPSPos
//                    && lastActuatorPSPos + accuracy >= dh.fb_actuatorPSPos
//                    && findingMinPS)
//            {
//                lastTimePS = System.nanoTime();
//
//                if (System.nanoTime() - lastTimePS >= 500000000)
//                {
//                    dh.cmd_actuatorPS = dh.fb_actuatorPSPos;
//                    dh.cmd_actuatorPSMinPos = dh.fb_actuatorPSPos;
//                    findingMinPS = false;
//                }
//
//            }
//
//            lastActuatorSBPos = dh.fb_actuatorSBPos;
//            if (lastActuatorSBPos - accuracy <= dh.fb_actuatorSBPos
//                    && lastActuatorSBPos + accuracy >= dh.fb_actuatorSBPos
//                    && findingMinSB)
//            {
//                lastTimeSB = System.nanoTime();
//
//                if (System.nanoTime() - lastTimeSB >= 500000000)
//                {
//                    dh.cmd_actuatorSB = dh.fb_actuatorSBPos;
//                    dh.cmd_actuatorSBMinPos = dh.fb_actuatorSBPos;
//                    findingMinSB = false;
//                }
//            }
//        }
//
//        //Move actuators to maximum pos
//        dh.cmd_actuatorPS = 4000;
//        dh.cmd_actuatorSB = 4000;
//        while (findingMaxPS && findingMaxSB)
//
//        {
//            lastActuatorPSPos = dh.fb_actuatorPSPos;
//            if (lastActuatorPSPos - accuracy <= dh.fb_actuatorPSPos
//                    && lastActuatorPSPos + accuracy >= dh.fb_actuatorPSPos
//                    && findingMaxPS)
//            {
//                lastTimePS = System.nanoTime();
//
//                if (System.nanoTime() - lastTimePS >= 500000000)
//                {
//                    dh.cmd_actuatorPS = dh.fb_actuatorPSPos;
//                    dh.cmd_actuatorPSMaxPos = dh.fb_actuatorPSPos;
//                    findingMaxPS = false;
//                }
//
//            }
//
//            lastActuatorSBPos = dh.fb_actuatorSBPos;
//            if (lastActuatorSBPos - accuracy <= dh.fb_actuatorSBPos
//                    && lastActuatorSBPos + accuracy >= dh.fb_actuatorSBPos
//                    && findingMaxSB)
//            {
//                lastTimeSB = System.nanoTime();
//
//                if (System.nanoTime() - lastTimeSB >= 500000000)
//                {
//                    dh.cmd_actuatorSB = dh.fb_actuatorSBPos;
//                    dh.cmd_actuatorSBMinPos = dh.fb_actuatorSBPos;
//                    findingMaxSB = false;
//                }
//
//            }
//        }
//
//        //Set wings to neutraPos
//        int middlePSPos = dh.getCmd_actuatorPSMaxPos() - dh.getCmd_actuatorPSMinPos();
//        dh.cmd_actuatorPS = middlePSPos;
//
//        int middleSBPos = dh.getCmd_actuatorSBMaxPos() - dh.getCmd_actuatorSBMinPos();
//        dh.cmd_actuatorSB = middleSBPos;
//    }
//
//    //Test lights
//    public void testLights()
//    {
//        boolean testingLights = true;
//        long lastTime = System.nanoTime();
//        int lightIntensity = 1;
//        while (testingLights && lightIntensity < 100)
//        {
//            if (System.nanoTime() - lastTime >= 250000000)
//            {
//                dh.cmd_lightMode = lightIntensity + 1;
//                lastTime = System.nanoTime();
//            }
//
//        }
//        try
//        {
//            Thread.sleep(5000);
//            dh.cmd_lightMode = 0;
//        } catch (Exception e)
//        {
//        }
//
//    }
}
