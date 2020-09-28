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

import ROV.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The alarm handler of the ROV is responsible for handling any alarms boolean
 * alarms or time based alarms on the ROV.
 *
 *
 * Not yet implemented
 */
public class AlarmHandler implements Runnable {

//    long lastTime = 0;
//    long elapsedTime = 0;
//
//    Data dh = null;
//    private ConcurrentHashMap<String, Boolean> listOfBooleanAlarms = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, Boolean> listOfTimerAlarms = new ConcurrentHashMap<>();
//    // Input data for the alarms to deside if the alarm is in alarm state or not
//
//    /**
//     * A list over the necesarry data for the alarms to function.
//     */
//    public ConcurrentHashMap<String, Integer> alarmDataList = new ConcurrentHashMap<>();
//    // List over all active or dormant alarms
//
//    /**
//     *
//     */
//    public ConcurrentHashMap<String, Boolean> completeAlarmList = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, Boolean> lastCompleteAlarmList = new ConcurrentHashMap<>();
//
//    /**
//     * A list over all inhibited alarms
//     */
//    public ConcurrentHashMap<String, Boolean> inhibitedAlarms = new ConcurrentHashMap<>();
//
//    /**
//     * 
//     */
//    public boolean ack = false;
//
//    /**
//     *
//     */
//    public boolean inhibit_waterLeakSensor_1_Alarm = false;
//
//    /**
//     * Timebased alarms
//     *
//     * @param dh
//     */
//    TimeBasedAlarms sbActuatorFeedbackAlarm;
//
//    /**
//     *
//     * @param dh
//     */
//    BooleanBasedAlarms waterLeakSensor_1_Alarm;
//
//    /**
//     *
//     * @param dh
//     */
//    public AlarmHandler(Data dh)
//    {
//        this.dh = dh;
//    }
//
//    /**
//     *
//     */
    public void run() {
//        fillAlarmListWithAlarms();
//        fillCompleteAlarmList();
//
//        updateAlarmInputData();
//        initiateAlarmThreads();
//
//        lastCompleteAlarmList.putAll(completeAlarmList);
//        inhibitedAlarms.putAll(completeAlarmList);
//        updateDataHandlerWithAlarms();
//
//        while (true)
//        {
//            if (System.nanoTime() - lastTime >= 250000000)
//            {
//                checkForAck();
//                
//                lastTime = System.nanoTime();
//            }
//
//            updateAlarmInputData();
//
//            if (!completeAlarmList.equals(lastCompleteAlarmList))
//            {
//                System.out.println("An alarm has been changed");
//                lastCompleteAlarmList.putAll(completeAlarmList);
//                updateDataHandlerWithAlarms();
//            } else
//            {
//                // System.out.println("Nothing");
//                //lastCompleteAlarmList.putAll(completeAlarmList);
//            }
//        }
//    }
//
//    private boolean isAck()
//    {
//        return ack;
//    }
//
//    private void setAck(boolean ack)
//    {
//        this.ack = ack;
//    }
//
//    private void checkForAck()
//    {
//        if (dh.isCmd_ack() && !ack)
//        {
//            System.out.println("Ack is True");
//            ack = true;
//            //setAck(true);
//        }
//        if (!dh.isCmd_ack() && ack)
//        {
//            ack = false;
//            //setAck(false);
//        }
//    }
//
//    private void fillCompleteAlarmList()
//    {
//        for (Entry e : listOfBooleanAlarms.entrySet())
//        {
//            String key = (String) e.getKey();
//            Boolean value = (Boolean) e.getValue();
//            completeAlarmList.put(key, value);
//        }
//        for (Entry e : listOfTimerAlarms.entrySet())
//        {
//            String key = (String) e.getKey();
//            Boolean value = (Boolean) e.getValue();
//            completeAlarmList.put(key, value);
//        }
//    }
//
//    private void fillAlarmListWithAlarms()
//    {
//        //Boolean alarms
//        listOfBooleanAlarms.put("waterLeakSensor_1_Alarm", false);
//
//        //Timer alarms
//        listOfTimerAlarms.put("sbActuatorFeedbackAlarm", false);
//    }
//
//    private void initiateAlarmThreads()
//    {
//        //Boolean Based Alarms
//        waterLeakSensor_1_Alarm = new BooleanBasedAlarms(dh,
//                this, "fb_waterLeakChannel_1", 1, "waterLeakSensor_1_Alarm",
//                true, ack, inhibit_waterLeakSensor_1_Alarm);
//
//        //Create threads
//        Thread waterLeakSensor_1_Alarm_Thread = new Thread(waterLeakSensor_1_Alarm);
//        Thread sbActuatorFeedbackAlarm_Thread = new Thread(sbActuatorFeedbackAlarm);
//
//        //Name threads
//        waterLeakSensor_1_Alarm_Thread.setName("waterLeakSensor_1_Alarm_Thread");
//        sbActuatorFeedbackAlarm_Thread.setName("sbActuatorFeedbackAlarm");
//
//        //Start threads        
//        sbActuatorFeedbackAlarm_Thread.start();
//        waterLeakSensor_1_Alarm_Thread.start();
//    }
//
//    private void updateAlarmInputData()
//    {
////        alarmDataList.put("fb_waterLeakChannel_1", dh.isFb_waterLeakChannel_1() ? 1 : 0);
//    }
//
//    private void updateDataHandlerWithAlarms()
//    {
//        dh.completeAlarmListDh.putAll(completeAlarmList);
//    }
//
////    private void buildalarmListFrom()
////    {
////        for (Entry e : alarmDataList.entrySet())
////        {
////            String key = (String) e.getKey();            
////            alarmList.put(key, false);
////        }       
////        
    }
}
