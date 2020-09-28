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
package ROV.AlarmSystem;

import ROV.Data;

/**
 * Creates boolean alarms for the ROV
 *
 *
 * Not yet implemented
 */
public class BooleanBasedAlarms implements Runnable {

//    long currentTime = 0;
//    long lastTime = 0;
//
//    String input;
//    int setPoint;
//    Boolean alarm;
//    boolean HAlarm;
//    boolean ack;
//    boolean inhibit;
//    String alarmName;
//    Data dh;
//    AlarmHandler alarmHandler;
//    
//
//    public BooleanBasedAlarms(Data dh, AlarmHandler alarmHandler, String input, int setPoint, String alarmName,
//            boolean HAlarm, boolean ack, boolean inhibit)
//    {
//        this.alarmHandler = alarmHandler;
//        this.dh = dh;
//        this.input = input;
//        this.setPoint = setPoint;
//
//        this.alarmName = alarmName;
//        this.HAlarm = HAlarm;
//        this.ack = ack;
//        this.inhibit = inhibit;
//
//        currentTime = System.nanoTime();
//
//    }
    public void run() {
//
//        while (true)
//        {
//            while (!alarmHandler.inhibitedAlarms.get(alarmName))
//            {
//                if (HAlarm && alarmHandler.alarmDataList.get(input) >= setPoint)
//                {
//                    // High Alarm
//                    alarm = true;
//
//                    alarmHandler.completeAlarmList.put(alarmName, alarm);
//                    //dh.handleDataFromAlarmList(alarmName, alarm);
//                    while (alarm)
//                    {
//                        try
//                        {
//                            Thread.sleep(10);
//                        } catch (Exception e)
//                        {
//                        }
//                        //System.out.println("waiting...");
//                        if (alarmHandler.ack)
//                        {
//                            //System.out.println("test");
//                            alarm = false;
//                            alarmHandler.completeAlarmList.put(alarmName, alarm);
//                            System.out.println("Alarm is acked");
//                            //dh.handleDataFromAlarmList(alarmName, alarm);
//                        }
//                    }
//                }
//                if (!HAlarm && alarmHandler.alarmDataList.get(input) <= setPoint)
//                {
//                    // Low Alarm
//                    alarm = true;
//                    alarmHandler.completeAlarmList.put(alarmName, alarm);
//                    //dh.handleDataFromAlarmList(alarmName, alarm);
//                    while (alarm)
//                    {
//                        if (alarmHandler.ack)
//                        {
//                            alarm = false;
//                            alarmHandler.completeAlarmList.put(alarmName, alarm);
//                            System.out.println("Alarm is acked");
//                            //dh.handleDataFromAlarmList(alarmName, alarm);
//                        }
//                    }
//                }
//            }
//
//        }
    }

}
