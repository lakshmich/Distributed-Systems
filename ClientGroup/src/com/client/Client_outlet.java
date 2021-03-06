package com.client;

import inter.BackInterface_replica;
import inter.Constant;
import inter.TestRemote;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

//****************** Outlet Device *********************//
public class Client_outlet {
    public static int id=3043;
    public static Boolean isLeader=true;
    static BackInterface_replica remote_data;
    public static TestRemote remote;

//    public static void startElection() throws IOException
//    {
//        String hostName="localhost";
//
//
//        ArrayList<Integer> otherPorts=new ArrayList<Integer>();
////        otherPorts.add(3042);
////        otherPorts.add(3043);
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
    //logic for Berkeley time synchronization
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


    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, IOException{

        com.client.Client_Impl_dev implDev =new com.client.Client_Impl_dev();

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

        Registry dev_register = LocateRegistry.createRegistry(2005);
        dev_register.bind(Constant.RMI_ID, implDev);



        //Global ID of Outlet Device
        System.out.println("Global ID of outlet device is "+ remote.register("device","outlet") + " at port " + serverId);

    }

}
