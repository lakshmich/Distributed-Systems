package com.client;

//imports remote interfaces
import inter.BackInterface_replica;
import inter.Constant;
import inter.TestRemote;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

//imports necessary packages

//********* TEMPERATURE SENSOR ***********//
public class Client_temp {

    public static BackInterface_replica remote_data;
    public static TestRemote remote;
    public int cnt_temp;
    public static Long logicalTime=0L;
    public static Long startTime;
    public static List<Long> lastTime=new ArrayList<Long>();
    public static HashMap<String,String> lines = new HashMap<String, String>();

    static int id=3041;
    public static Boolean isLeader=true;




    //this run reads temperature inputs from csv file
    public void run() throws IOException {
        try {
            while(true)
            {
                Long cnt_tym = (System.currentTimeMillis()/1000);
                Set ts=lines.keySet();
                logicalTime = cnt_tym - startTime;                          //logicalTime is the time at which input is read according to the time stamp. (from the startTime of the class)

                if(ts.contains(logicalTime.toString()) && !lastTime.contains(logicalTime)){
                    lastTime.add(logicalTime);
                    System.out.print("Logical=" + logicalTime + " ");
                    cnt_temp = Integer.parseInt(lines.get(logicalTime.toString()));
                    System.out.println("Temperature - " + cnt_temp);       //cnt_temp is temperature read from csv file
                    remote_data.newEntry(1,cnt_temp,logicalTime);                 //each input read is entered sent to the log file of database tier
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {

        com.client.Client_Impl_sens implSens =new com.client.Client_Impl_sens();

        Registry sens_register = LocateRegistry.createRegistry(2009);     //port at which query
        sens_register.bind(Constant.RMI_ID, implSens);

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

        Client_temp st = new Client_temp();

        System.out.println("Global ID of temperature sensor is "+ remote.register("sensor","temperature") + " at port " + serverId);    //register method is called(which is the service provided by the gateway server) and then prints the global ID of temperature sensor

//        ArrayList<Integer> allPorts=new ArrayList<Integer>();
//        allPorts.add(3041);
////        allPorts.add(3042);
////        allPorts.add(3043);
////        allPorts.add(3044);
////        allPorts.add(3045);
//
////        ArrayList<socketThread> sockets=new ArrayList<socketThread>();
//
//        socketThread[] sockets=new socketThread[5];
//        String hostName="localhost";
//        int id=0;
//        for(int p: allPorts)
//        {
//            sockets[id] =new socketThread(hostName,p);
//            sockets[id].start();
//            id++;
//            if(id>4)
//                break;
//        }
//
//        startElection();
        startTime = System.currentTimeMillis()/1000;           //indicates the time at which this class file is initially run
        String path=new File("").getAbsolutePath() + "\\test-input.csv";          //path, here, indicates the location where the test-input csv file is located

        BufferedReader br = null;             //reads the test-input csv file
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        String[] parts;
        while(((line = br.readLine()) != null)) {           // "lines" is a hashmap in which the timestamp and the door state w.r.t the timestamp are key and values
            parts=line.split(",");
            lines.put(parts[0],parts[1]);
        }

        st.run();           // starting the run method which reads the temperature input from csv file
    }
}