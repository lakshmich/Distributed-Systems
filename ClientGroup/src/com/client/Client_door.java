/**
 * Created by sahitya on 5/4/2015.
 */
package com.client;

//imports all necessary interfaces
import inter.BackInterface_replica;
import inter.Constant;
import inter.TestRemote;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Client_door extends Thread{

    //    public static int id=5;
    public static int state=1;
    public static TestRemote remote;
    public static Long startTime;
    public static Long logicalTime=0L;
    public static HashMap<String,String> lines = new HashMap<String, String>();
    public static List<Long> lastTime=new ArrayList<Long>();
    public int LC_door;
    public static BackInterface_replica remote_data;

    public static int id=3045;   //Socket port id of client, which is being used as an identifier for process
    public static Boolean isLeader=true;




    //this class has a thread which reads door's state from csv file
    public static class push_thread extends TimerTask{
        public void run() {
            try {
                while (true) {
                    Long cnt_tym = (System.currentTimeMillis() / 1000);          //indicates the current time of each run
                    Set ts = lines.keySet();
                    logicalTime = cnt_tym - startTime;                        //logicalTime is the time same as the time stamp of csv file at which door's state is read from csv file

                    if (ts.contains(logicalTime.toString()) && !lastTime.contains(logicalTime)) {
                        lastTime.add(logicalTime);
                        System.out.print("Logical=" + logicalTime + " ");
                        state = Integer.parseInt(lines.get(logicalTime.toString()));
                        System.out.println("Door - " + state);
//                        remote_data.newEntry(5,state,logicalTime);                     //each input read is entered sent to the log file of database tier
                        try {
                            remote.report_state_by_sensor(5, state, logicalTime);     //door pushes it current state through the remote method report_state_b_sensor
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, IOException {

        Client_Impl_door door_impl = new Client_Impl_door();
        Registry door_register = LocateRegistry.createRegistry(4000);     //creates a registry at port 4000
        door_register.bind(Constant.RMI_ID, door_impl);

        Random r = new Random();

        int serverId=(r.nextBoolean())? Constant.RMI_port:Constant.RMI_port_replica;
        int gatewayId=(r.nextBoolean())?6000:8000;

        try {
            Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, serverId);    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
            remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
        }catch(ConnectException e){
            if (serverId == Constant.RMI_port){
                Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port_replica);    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
            }
            else if(serverId == Constant.RMI_port_replica){
                Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port);    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
            }
        }

        Registry back_register = LocateRegistry.getRegistry("localhost", gatewayId);   //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
        remote_data = (BackInterface_replica) back_register.lookup("localhost");

        //GlobalID of door sensor
        int doorID = remote.register("sensor", "door");
        System.out.println( "Global ID of bulb device is " + doorID + " at port " + serverId);



        Timer t = new Timer();   // Instantiate Timer Object
        push_thread st = new push_thread();            // Instantiate ScheduledTask class

        startTime=System.currentTimeMillis()/1000;     //indicates the time at which this class file is initially run

        String path=new File("").getAbsolutePath() + "\\test-input.csv";   //path, here, indicates the location where the test-input csv file is located

        BufferedReader br = null;                                 //reads the test-input csv file
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        String[] parts;
        while(((line = br.readLine()) != null)) {             // "lines" is a hashmap in which the timestamp and the door state w.r.t the timestamp are key and values
            parts=line.split(",");
            lines.put(parts[0],parts[4]);
        }
        t.schedule(st,0,15000);                           //push thread, which reads inputs from the hashmap and pushes door's state to server, starts at scheduled time

    }
}

