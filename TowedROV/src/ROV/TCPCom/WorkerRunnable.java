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
package ROV.TCPCom;

import ROV.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * Code taken from:
 * http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 *
 * Responsible for handling data from the client and answering the client. All
 * client commands are listed in the switch case
 *
 */
public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;

    Data dh = null;

    StartupCalibration StartupCalibration = null;
    String start_char = "<";
    String end_char = ">";
    String sep_char = ":";
    int manualPos = 1000;

    /**
     * The constructor for the WorkerRunnable class
     *
     * @param clientSocket The socket the client is connecte dto
     *
     * @param dh the shared recource data class
     */
    public WorkerRunnable(Socket clientSocket, Data dh) {
        this.clientSocket = clientSocket;

        this.dh = dh;

    }

    /**
     * Responsible for handling data from the client and answering the client.
     * All client commands are listed in the switch case
     */
    public void run() {
        boolean clientOnline = true;
//        boolean welcomeMessageIsSent = false;
        try {
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(
                            this.clientSocket.getInputStream()));

            PrintWriter outToClient = new PrintWriter(
                    this.clientSocket.getOutputStream(), true);

            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            while (clientOnline) {
                if (inFromClient.ready()) {
                    String key = "";
                    String value = "";
                    String inputData = inFromClient.readLine();
                    if (inputData.contains("<") && inputData.contains(">")) {
                        inputData = inputData.substring(inputData.indexOf(start_char) + 1);
                        inputData = inputData.substring(0, inputData.indexOf(end_char));
                        inputData = inputData.replace("?", "");
                        if (inputData.contains(":")) {
                            String[] data = inputData.split(sep_char);
                            key = data[0];
                            value = data[1];
                        } else {
                            String[] data = inputData.split(sep_char);
                            key = data[0];
                        }

                    } else {
                        key = (String) inputData;
                    }
                    if (!dh.getFb_ROVReady()) {
                        outToClient.println("Server: ROV not ready");
                    } else {

                        switch (key) {
                            //Commands
                            case "cmd_resetSteppers":
                                dh.setDataToSerial("reset_0");
                                System.out.println("reset steppers");
                                outToClient.println("Server: OK");
                                break;
                            case "cmd_stepperManualUp":
                                this.manualPos = manualPos + 50;
                                dh.setDataToSerial("manUp_" + Integer.toString(manualPos));
                                System.out.println("moving actuator up: " + value);
                                outToClient.println("Server: OK");
                                break;
                            case "cmd_stepperManualDown":
                                this.manualPos = manualPos - 50;
                                dh.setDataToSerial("manDown_" + Integer.toString(manualPos));
                                System.out.println("moving actuator down: " + value);
                                outToClient.println("Server: OK");
                                break;
                            case "cmd_lightMode":
                                dh.setCmd_lightMode(parseStringToInt(value));
                                System.out.println("LightMode: " + dh.getCmd_lightMode());
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_targetDistance":
                                dh.setDataToSerial("Setpoint_" + Double.toString(Double.valueOf(value)));
                                dh.setCmd_targetDistance(Double.valueOf(value));
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_pid_p":
                                dh.setDataToSerial("P_" + Double.toString(Double.valueOf(value)));
                                dh.setCmd_pid_p(Double.valueOf(value));
//                                System.out.println("Pid_p is: " + dh.getCmd_pid_p());
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_pid_i":
                                dh.setDataToSerial("I_" + Double.toString(Double.valueOf(value)));
                                dh.setCmd_pid_i(Double.valueOf(value));
//                                System.out.println("Pid_i is: " + dh.getCmd_pid_i());
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_pid_d":
                                dh.setDataToSerial("D_" + Double.toString(Double.valueOf(value)));
                                dh.setCmd_pid_d(Double.valueOf(value));
//                                System.out.println("Pid_d is: " + dh.getCmd_pid_d());
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_emergencySurface":
                                dh.setDataToSerial("emergency_0");
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_rovDepth":
                                dh.setDataToSerial("Depth_" + Double.toString(Double.valueOf(value)));
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_targetMode":
                                if (value.equals("2")) {
                                    manualPos = 1000;
                                    dh.setDataToSerial("1000");
                                }
                                dh.setcmd_targetMode(parseStringToInt(value));
                                outToClient.println("Server: OK");
                                break;

                            case "cmd_offsetDepthBeneathROV":
                                dh.setDataToSerial("Offset-seafloor_" + Double.toString(Double.valueOf(value)));
                                outToClient.println("Server: OK");
                                break;
                            case "cmd_offsetROVdepth":
                                dh.setDataToSerial("Offset-depth_" + Double.toString(Double.valueOf(value)));
                                dh.setCmd_offsetROVdepth(Double.valueOf(value));
                                outToClient.println("Server: OK");
                                break;

                            //Feedback commands
                            case "fb_allData":
                                outToClient.println(dh.getDataToSend());
                                //System.out.println("Sent all data");
                                break;

                            case "fb_depthToSeabedEcho":
                                outToClient.println("<Fb_depthBeneathROV:" + dh.getFb_depthBeneathROV() + ">");
                                break;

                            case "fb_speedThroughWather":
                                outToClient.println("<fb_speedThroughWather:" + dh.getFb_speedThroughWather() + ">");
                                break;

                            case "fb_waterTemperature":
                                outToClient.println("<fb_waterTemperature:" + dh.getFb_waterTemperature() + ">");
                                break;

                            case "fb_stepperPSPos":
                                outToClient.println("<fb_stepperPSPos:" + dh.getFb_stepperPos() + ">");
                                break;

                            case "fb_stepperSBPos":
                                outToClient.println("<fb_stepperSBPos:" + dh.getFb_stepperPos() + ">");
                                break;

                            case "fb_tempMainElBoxFront":
                                outToClient.println("<fb_tempMainElBox:" + dh.getFb_tempMainElBoxFront() + ">");
                                break;

                            case "fb_tempMainElBoxRear":
                                outToClient.println("<fb_tempMainElBox:" + dh.getFb_tempMainElBoxRear() + ">");
                                break;

                            case "fb_currentDraw":
                                outToClient.println("<fb_currentDraw:" + dh.getFb_currentDraw() + ">");
                                break;

                            case "fb_pitchAngel":
                                outToClient.println("<fb_pitchAngle:" + dh.getFb_pitchAngle() + ">");
                                break;

                            case "fb_rollAngle":
                                outToClient.println("<fb_rollAngle:" + dh.getFb_rollAngle() + ">");
                                break;

                            case "fb_heading":
                                outToClient.println("<fb_heading:" + dh.getFb_heading() + ">");
                                break;

                            //Stored Commands
                            case "get_cmd_lightMode":
                                outToClient.println("<get_cmd_lightMode:" + dh.getCmd_lightMode() + ">");
                                break;

                            case "get_cmd_pid_p":
                                outToClient.println("<get_cmd_pid_p:" + dh.getCmd_pid_p() + ">");
                                break;

                            case "get_cmd_pid_i":
                                outToClient.println("<get_cmd_pid_i:" + dh.getCmd_pid_i() + ">");
                                break;

                            case "get_cmd_pid_d":
                                outToClient.println("<get_cmd_pid_d:" + dh.getCmd_pid_d() + ">");
                                break;

                            case "get_cmd_pid_gain":
                                outToClient.println("<get_cmd_pid_gain:" + dh.getCmd_pid_gain() + ">");
                                break;

                            case "get_ROVComPorts":
                                String portListString = "<";
                                for (Entry e : dh.comPortList.entrySet()) {
                                    String comPortKey = (String) e.getKey();
                                    String comPortValue = (String) e.getValue();
                                    portListString = portListString + comPortKey + ":" + comPortValue + ":";
                                }
                                portListString = portListString + ">";
                                outToClient.println(portListString);
                                break;

                            //Other  commands
                            case "ping":
                                //output.write(("<ping:true>").getBytes());
                                dh.setCmd_ping(true);
                                outToClient.println("<ping:true>");
//                                welcomeMessageIsSent = true;
                                break;

                            case "ack":
                                if (dh.isCmd_ack()) {
                                    dh.setCmd_ack(false);
                                } else {
                                    dh.setCmd_ack(true);
                                }
                                outToClient.println("Ack: " + dh.isCmd_ack());
                                break;

                            case "getAlarms":
                                String completeAlarmListString = "<";

                                for (Map.Entry e : dh.completeAlarmListDh.entrySet()) {
                                    key = (String) e.getKey();
                                    if (e.getValue().equals(true)) {
                                        value = "true";
                                    } else {
                                        value = "false";
                                    }

                                    completeAlarmListString = completeAlarmListString + key + ":" + value + ":";
                                }
                                outToClient.println(completeAlarmListString + ">");
                                break;

                            case "exit":
                                output.close();
                                input.close();
                                clientOnline = false;
                                break;

                            default:
                                outToClient.println("Error: Not a command");
                                break;

                        }
                    }
                }

            }

        } catch (IOException e) {
            //report exception somewhere.
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private boolean parseStringToBoolean(String value) {
        Boolean result = false;
        try {
            result = Boolean.valueOf(value);
        } catch (Exception e) {
            System.out.println("Exception while parsing to double");
        }
        return result;
    }

    private Integer parseStringToInt(String value) {
        Integer result = 0;

        try {
            result = Integer.valueOf(value);
        } catch (Exception e) {
            System.out.println("Exception while parsing to integer");
        }

        return result;
    }

}
