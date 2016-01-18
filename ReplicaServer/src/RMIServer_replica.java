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

class Semi_replica extends Thread {

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
    Semi_replica(int a) throws RemoteException, NotBoundException {
        Registry sens_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2007);
        remote_sens = (ClientRemoteSens) sens_register.lookup(Constant.RMI_ID);
    }
    /* Registers created at both the clients motion sensor and outlet device
     * is being looked up here. A thread is started which
     * */
    Semi_replica() throws IOException, NotBoundException {
        Registry sens_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2007);
        remote_sens = (ClientRemoteSens) sens_register.lookup(Constant.RMI_ID);

        Registry dev_register = LocateRegistry.getRegistry(Constant.RMI_ID, 2005);
        remoteDe = (ClientRemote) (dev_register.lookup(Constant.RMI_ID));

        startTime = System.currentTimeMillis();

//		String path = new File("").getAbsolutePath() + "\\test-input.csv";
//
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(path));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		String line;
//		String[] parts;
//		while(((line = br.readLine()) != null)) {
//			parts=line.split(",");
//			lines_motion.put(parts[0],parts[2]);
//			lines_door.put(parts[0],parts[4]);
//		}


//		t1 = new Thread(this, "3");
//		t1.start();

    }

    //logic for task-1 goes here
//	public void run() {
//		// TODO Auto-generated method stub
//
//		boolean running = true;
//		if (t1.getName().equals("3")) {
//
//			while (running) {
//				try {
//					Long cnt_tym = (System.currentTimeMillis() / 1000);
//					Set ts = lines.keySet();
//
//					if(cnt_tym-startTime<logicalTime)
//					logicalTime=logicalTime+1;
//					else
//					logicalTime = cnt_tym - startTime;
//
//					if (ts.contains(logicalTime.toString()) && !lastTime.contains(logicalTime)) {
//						lastTime.add(logicalTime);
//
//						a_query_temp = Integer.parseInt(lines.get(logicalTime.toString()));
//
//					}
//
//					if (a_query_temp < 1) {
//						remoteDe.change_state(3, 1);
//						rmi.remote_data.newEntry(3, 1);
//					} else
//						if (a_query_temp > 2) {
//								remoteDe.change_state(3, 0);
//								rmi.remote_data.newEntry(3, 0);
//						}
//
//					System.out.println("Outlet device - " + remote_sens.query_state(3) + "\n");
////						}
////					}
//					}catch(RemoteException e){
//						e.printStackTrace();
////					}catch(FileNotFoundException e){
////						e.printStackTrace();
//					}catch(IOException e){
//						e.printStackTrace();
//					}
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					if (Thread.interrupted())
//						return;
//				}
//
//		}
//	}
}


class Server_Impl_replica extends UnicastRemoteObject implements TestRemote {

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

    protected Server_Impl_replica() throws IOException, NotBoundException {
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

    @Override
    public void heartbeat(int status) throws RemoteException {
        this.server_status = status;
        if(status ==1 )
            System.out.println(" Gateway server says : I am alive");
        else System.out.println("Gateway server is failed");
    }

    @Override
    public void heartbeat_replica(int status) throws RemoteException {

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

public  class RMIServer_replica extends Thread {

//	public Long logicalTime = 0L;

    public static HashMap<String,String> lines_motion = new HashMap<String, String>();
    public static HashMap<String,String> lines_door = new HashMap<String, String>();
    public static TestRemote front_data;

    public static BackInterface_replica remote_data;

    public void run() {
        Random r = new Random();
        int status = r.nextInt(2);
        try {
            front_data.heartbeat_replica(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException, IOException {

        Server_Impl_replica impl = new Server_Impl_replica();
        Registry register = LocateRegistry.createRegistry(Constant.RMI_port_replica);      //creates the registry at port 2001 for remote clients to use remote services provided by gateway server
        register.bind(Constant.RMI_ID, impl);

        Registry back_register = LocateRegistry.getRegistry("localhost", 8000);             //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
        remote_data = (BackInterface_replica) back_register.lookup("localhost");

        Registry front_register = LocateRegistry.getRegistry("localhost", 2001);             //gets the registry whose implementation is bound to port 6000 to record the door's state periodically into the log file
        front_data = (TestRemote) front_register.lookup("localhost");

        System.out.println("Server is started\n");

        RMIServer_replica trd = new RMIServer_replica();
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
