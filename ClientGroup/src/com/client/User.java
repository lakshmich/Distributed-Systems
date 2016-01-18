package com.client;

import inter.Constant;
import inter.TestRemote;

import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sahitya on 3/28/2015.
 */
public class User{
    public static String mode;
    public static TestRemote remote;
    public int LC_user;

    public static class mode_thread extends TimerTask{

        public void run(){
            Random bul = new Random();
            int Bul = bul.nextInt(2);

            if(String.valueOf(Bul).equals("1"))
                mode = "HOME";
            else mode = "AWAY";

            try {
                remote.change_mode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            System.out.println(mode);

        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException {

        Random r = new Random();

        int serverId=(r.nextBoolean())? Constant.RMI_port:Constant.RMI_port_replica;

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

        Timer time = new Timer();
        mode_thread md = new mode_thread();
        time.schedule(md,0,19000);
    }
}