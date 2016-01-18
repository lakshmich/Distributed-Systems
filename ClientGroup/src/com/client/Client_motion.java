package com.client;

import inter.BackInterface_replica;
import inter.Constant;
import inter.TestRemote;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

//import Re TestRemote;

//****************** Motion Sensor *********************//
public class Client_motion extends Thread {
    //	public static int id=2;
    public static int motion = 1;
    public static TestRemote remote;
    public static BackInterface_replica remote_data;
    public static int cnt_motion;
    public static Long startTime;
    public static Long logicalTime = 0L;
    public static HashMap<String, String> lines = new HashMap<String, String>();
    public static List<Long> lastTime = new ArrayList<Long>();

    public static int id = 3042;
    public static Boolean isLeader = true;
    public static int serverId;
    public static int gatewayId;


//    public static void startElection() throws IOException
//    {
//        String hostName="localhost";
//
//
//        ArrayList<Integer> otherPorts=new ArrayList<Integer>();
////        otherPorts.add(3042);
//        otherPorts.add(3043);
//        otherPorts.add(3044);
//        otherPorts.add(3045);
//
//        //start election process with all other processes with ids greater than my process id
//
//        for(int p: otherPorts)
//        {
//
//            try {
//                Socket kkSocket = new Socket(hostName, p);
//                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
//
//                //broadcast election method
//
//                out.print("election "+ Client_temp.id);
//
//                //if any other process with higher id exists then I drop out of the election
//
//                System.out.println("Process " + p + " exists. I can't be the leader");
//                isLeader=false;
//            }
//            catch(Exception e)
//            {
//                System.out.println("Process " + p + " doesnt exist");
//            }
//
//        }
//
//        //if no other process with higher id responds back, then declare me as winner
//
//        if(isLeader)
//            System.out.println("I won!");
//
//    }
//
//    //logic for Berkeley time synchronization
//    public static void startTimeSync() throws IOException, NotBoundException {
//        String hostName="localhost";
//
//
//        ArrayList<Integer> otherPorts=new ArrayList<Integer>();
//        otherPorts.add(3042);
//        otherPorts.add(3043);
//        otherPorts.add(3044);
//        otherPorts.add(3041);
//
//
//        //Berkeley time synchronization if Door is the leader
//
//        Long doorTime=System.currentTimeMillis();
//
//        Registry registry1 = LocateRegistry.getRegistry("localhost", 2005);
//        ClientRemote remote = (ClientRemote) registry1.lookup(Constant.RMI_ID);
//        Long outletTime=remote.get_time();
//        System.out.println("Time Received from outlet "+outletTime);
//
//        Registry registry2 = LocateRegistry.getRegistry("localhost", 2007);
//        ClientRemoteSens remoteSe = (ClientRemoteSens) registry2.lookup(Constant.RMI_ID);
//        Long motionTime=remoteSe.get_time();
//        System.out.println("Time Received from motion sensor "+motionTime);
//
//        Registry registry3 = LocateRegistry.getRegistry("localhost", 2003);
//        ClientRemote remoteBulb = (ClientRemote) registry3.lookup(Constant.RMI_ID);
//        Long bulbTime=remoteBulb.get_time();
//        System.out.println("Time Received from bulb "+ bulbTime);
//
//        Registry registry4 = LocateRegistry.getRegistry("localhost", 2009);
//        ClientRemoteSens remoteTemp = (ClientRemoteSens) registry4.lookup(Constant.RMI_ID);
//        Long tempTime=remoteTemp.get_time();
//        System.out.println("Time Received from temp sensor "+tempTime);
//
//        Long outletOffset=outletTime-doorTime;
//        Long motionOffset=motionTime-doorTime;
//        Long bulbOffset=bulbTime-doorTime;
//        Long tempOffset=tempTime-doorTime;
//
//        //Take the average of all offsets and use it next for synchronization
//        Long averageOffset=(outletOffset+motionOffset+bulbOffset+tempOffset)/4;
//
//        //Calculation offsets for each client participating in time synchronization
//        outletOffset=averageOffset-outletOffset;
//        motionOffset=averageOffset-motionOffset;
//        bulbOffset=averageOffset-bulbOffset;
//        tempOffset=averageOffset-tempOffset;
//
//        Long doorOffset=averageOffset;
//
//        System.out.println("Leader offset="+doorOffset);
//        System.out.println("Setting offsets");
//
//        //Setting offsets for other clients
//        remote.set_offset(outletOffset,motionOffset,bulbOffset,tempOffset);
//        remoteSe.set_offset(outletOffset,motionOffset,bulbOffset,tempOffset);
//        remoteBulb.set_offset(outletOffset,motionOffset,bulbOffset,tempOffset);
//        remoteTemp.set_offset(outletOffset,motionOffset,bulbOffset,tempOffset);
//
//    }


    public static class trd extends TimerTask {

        public void run() {
            while (true) {
                try {

                    Long cnt_tym = (System.currentTimeMillis() / 1000);           //indicates the current time of each run
                    Set ts = lines.keySet();
                    logicalTime = cnt_tym - startTime;                    //logicalTime is the time same as the time stamp of csv file at which motion sensor's state is read from csv file

                    if (ts.contains(logicalTime.toString()) && !lastTime.contains(logicalTime)) {
                        lastTime.add(logicalTime);
                        System.out.println("Logical=" + logicalTime + " ");
                        cnt_motion = Integer.parseInt(lines.get(logicalTime.toString()));
                        try {
                            remote.report_state_by_sensor(2, Integer.parseInt(lines.get(logicalTime.toString())), logicalTime);         //motion sensor pushes it's current state through the remote method report_state_b_sensor
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        remote_data.newEntry(2, cnt_motion, logicalTime);             //each input read is entered sent to the log file of database tier
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, serverId);    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                    remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
                } catch (ConnectException e) {
                    if (serverId == Constant.RMI_port) {
                        Registry registry1 = null;    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                        try {
                            registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port_replica);
                            serverId=Constant.RMI_port_replica;
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        } catch (NotBoundException e1) {
                            e1.printStackTrace();
                        }
                    } else if (serverId == Constant.RMI_port_replica) {
                        Registry registry1 = null;    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                        try {
                            registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port);
                            serverId=Constant.RMI_port;
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        } catch (NotBoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (AccessException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        public static void main(String[] args) throws IOException, NotBoundException, AlreadyBoundException {

            Random r = new Random();

            serverId = (r.nextBoolean()) ? Constant.RMI_port : Constant.RMI_port_replica;
            gatewayId = (r.nextBoolean()) ? 6000 : 8000;

            com.client.Client_Impl_sens implSens = new com.client.Client_Impl_sens();

            Registry sens_register = LocateRegistry.createRegistry(2007);   //creates a registry at port 2007
            sens_register.bind(Constant.RMI_ID, implSens);

            try {
                Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, serverId);    ////gets the registry which is created at port 2001 to use remote services provided by gateway server
                remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
            } catch (ConnectException e) {
                if (serverId == Constant.RMI_port) {
                    Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port_replica);    //gets the registry which is created at port 2001 to use remote services provided by gateway server
                    remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
                    serverId=Constant.RMI_port_replica;
                } else if (serverId == Constant.RMI_port_replica) {
                    Registry registry1 = LocateRegistry.getRegistry(Constant.RMI_ID, Constant.RMI_port);    //gets the registry which is created at port 2001 to use remote services provided by gateway server
                    remote = (TestRemote) registry1.lookup(Constant.RMI_ID);
                    serverId=Constant.RMI_port;
                }
            }

            Registry back_register = LocateRegistry.getRegistry("localhost", gatewayId);   //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
            remote_data = (BackInterface_replica) back_register.lookup("localhost");


            //Global ID of motion sensor
            int motionID = remote.register("sensor", "motion");
            System.out.println("Global ID of motion sensor is " + motionID + " at port " + serverId + "\n");

            startTime = System.currentTimeMillis() / 1000;
            Timer time = new Timer(); // Instantiate Timer Object
            trd st = new trd(); // Instantiate ScheduledTask class


            System.out.print(new File(".").getAbsolutePath());
            String path = new File("").getAbsolutePath() + "\\test-input.csv";     //path, here, indicates the location where the test-input csv file is located

            BufferedReader br = null;                     //reads the test-input csv file
            try {
                br = new BufferedReader(new FileReader(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String line;
            String[] parts;
            while (((line = br.readLine()) != null)) {           // "lines" is a hashmap in which the timestamp and the motion sensor state w.r.t the timestamp are key and values
                parts = line.split(",");
                lines.put(parts[0], parts[2]);
            }

            time.schedule(st, 0, 15000);                //push thread, which reads inputs from the hashmap and pushes motion sensor's state to server, starts at scheduled time


        }
}

