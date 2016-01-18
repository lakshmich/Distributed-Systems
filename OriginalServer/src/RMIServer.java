/**
 * Created by sahitya on 5/4/2015.
 */
//package com.test;

import com.inter.client.ClientRemote;
import com.inter.client.ClientRemoteSens;
import inter.BackInterface_replica;
import inter.Constant;
import inter.TestRemote;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Semi extends Thread {

    BackInterface_replica remote_data;
    int a_query_temp;
    ClientRemote remoteDe;
    ClientRemoteSens remote_sens;
    Long startTime = 0L;
    Long logicalTime = 0L;
    public static List<Long> lastTime=new ArrayList<Long>();

    /* registry created, at the motion sensor, which has query state method,
     * is being looked up and used here in the Server class
     * */
    Semi(int a) throws RemoteException, NotBoundException {
        Registry sens_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2007);
        remote_sens = (ClientRemoteSens) sens_register.lookup(Constant.RMI_ID);
    }
    /* Registers created at both the clients motion sensor and outlet device
     * is being looked up here. A thread is started which
     * */
    Semi() throws IOException, NotBoundException {
        Registry sens_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2007);
        remote_sens = (ClientRemoteSens) sens_register.lookup(Constant.RMI_ID);

        Registry dev_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2005);
        remoteDe = (ClientRemote) (dev_register.lookup(Constant.RMI_ID));

        startTime = System.currentTimeMillis();

    }
}


class Server_Impl extends UnicastRemoteObject implements TestRemote {

    private static final long serialVersionUID = 1L;
    public static int p, q, r = 3, s;
    public static String a;
    public int door_state;
    public int cnt_motion_state;
    public int prev_motion_state = 0;
    public static int motion_state;
    public static String mode = "home";
    long prev_time;
    long cnt_time;
    public static Long startTime;
    public static Long logicalTime = 0L;
    public static HashMap<String, String> lines = new HashMap<String, String>();
    public static List<Long> lastTime = new ArrayList<Long>();
    public Long lastMotionUpdate = 0L;
    public Long lastDoorUpdate = 0L;
    Semi_replica semi;
    Thread trd = new Thread();
    public int beacon_state;
    Boolean checkPerson = true;
    public int server_status;

    protected Server_Impl() throws IOException, NotBoundException {
        super();

        startTime = System.currentTimeMillis() / 1000;
        String path = new File("").getAbsolutePath() + "\\test-input.csv";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        String[] parts;
        while (((line = br.readLine()) != null)) {
            parts = line.split(",");
            lines.put(parts[0], parts[2]);
        }

    }

    //change mode
    public void change_mode(String mode) {
        this.mode = mode;
    }

    public void report_state_by_sensor(int id, int state, Long logicTime) throws RemoteException {

        if (id == 2) {
            this.cnt_motion_state = state;
            System.out.println("Now motion state is: " + cnt_motion_state + " at time " + logicTime);
            if (cnt_motion_state == 1)
                lastMotionUpdate = logicTime;

            if (lastMotionUpdate > lastDoorUpdate && cnt_motion_state == 1) {
                if (checkPerson == false) {
                    System.out.println("Person entered" + " at time " + lastMotionUpdate);
                    checkPerson = true;
                }
            }
            if (logicalTime < logicTime)
                logicalTime = logicTime + 1;
            else
                logicalTime = logicalTime + 1;
            System.out.println("Logical time now on server: " + logicalTime + " at time " + lastMotionUpdate);

//			if(lastMotionUpdate<lastDoorUpdate && cnt_motion_state==0)
//				System.out.println("Person left");
        }
        if (id == 5) {
            door_state = (state);
            lastDoorUpdate = logicTime;
            if (door_state == (1))
                System.out.println("door sensed" + " at time " + lastDoorUpdate);
            else System.out.println("door sensed" + " at time " + lastDoorUpdate);

//			if(lastMotionUpdate>lastDoorUpdate && door_state==1)
//				System.out.println("Person entered");

            if (lastMotionUpdate < lastDoorUpdate) {
                if (checkPerson) {
                    System.out.println("Person left" + " at time " + lastDoorUpdate);
                    checkPerson = false;
                }
            }
            if (logicalTime < logicTime)
                logicalTime = logicTime + 1;

            if (id == 6) {
                this.beacon_state = state;
            }
            System.out.println("Logical time now on server: " + logicalTime + " at time " + lastDoorUpdate);
        }
    }

    public void heartbeat(int status) throws RemoteException{

    }

    public void heartbeat_replica(int status) throws RemoteException{
        this.server_status = status;
        if(status == 1) {
            System.out.println("Gateway server replica says : I am alive");
        }else System.out.println("Gateway server replica is failed");
    }


    //register method- all clients which register are given global ID
    public int register(String type, String name) throws RemoteException, NotBoundException, IOException {
        if (type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("temperature")))
            return p = 1;

        //a thread is started to notify when motion is started
        if (type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("motion"))) {
            q = 2;

            semi = new Semi_replica(q);
            return q;
        }

        //instantiating semi class(which is in RMI Server) to signal server that client side registry has been created
        if (type.equalsIgnoreCase("device") && name.equalsIgnoreCase("outlet")) {
            r = 3;
            semi = new Semi_replica();
//			trd.start();
            return r;
        }
        if (type.equalsIgnoreCase("device") && (name.equalsIgnoreCase("bulb")))
            return s = 4;

        if (type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("door")))
            return 5;

        if (type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("beacon")))
            return 6;

        return 0;
    }

}

public  class RMIServer extends Thread{

//	public Long logicalTime = 0L;

    public static HashMap<String,String> lines_motion = new HashMap<String, String>();
    public static HashMap<String,String> lines_door = new HashMap<String, String>();

    public static TestRemote replica_server;
    public static BackInterface_replica remote_data;

    public void run(){
        Random r = new Random();
        int status = r.nextInt(2);
        try {
            replica_server.heartbeat(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, IOException, InterruptedException {

        Server_Impl impl = new Server_Impl();
        Registry register = LocateRegistry.createRegistry(Constant.RMI_port);      //creates the registry at port 2001 for remote clients to use remote services provided by gateway server
        register.bind(Constant.RMI_ID, impl);

        Thread.sleep(30000);

        Registry back_register = LocateRegistry.getRegistry("localhost", 6000);             //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
        remote_data = (BackInterface_replica) back_register.lookup("localhost");

        Registry replica_register = LocateRegistry.getRegistry("localhost", 2015);             //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
        replica_server = (TestRemote) replica_register.lookup("localhost");


        System.out.println("Server is started\n");

        RMIServer trd = new RMIServer();
        trd.start();

        String path = new File("").getAbsolutePath() + "\\test-input.csv";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        String[] parts;
        while(((line = br.readLine()) != null)) {
            parts=line.split(",");
            lines_motion.put(parts[0],parts[2]);
            lines_door.put(parts[0],parts[4]);
        }


    }
}
