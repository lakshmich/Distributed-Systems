package com.client;

import com.inter.client.ClientRemoteSens;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Client_Impl_sens extends UnicastRemoteObject implements ClientRemoteSens {
    int outlet_state;
    int bulb_state;
    int motion_state;
    int door_state;
    Long outletOffset=0L;
    Long motionOffset=0L;
    Long bulbOffset=0L;
    Long tempOffset=0L;

    Client_temp tmp;
    Client_motion mot;

    protected Client_Impl_sens() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

    public int query_state(int id) throws RemoteException {

        switch(id){

            //temperature is randomly chosen between -10 C and 15 C
            case 1:
                tmp = new Client_temp();
                return tmp.cnt_temp;
//			int START = -10;
//			int END = 15;
//			Random random = new Random();
//
//		      Integer x = showRandomInteger(START, END, random);
//
//			return x;

            //motion is randomly chosen yes or no
            case 2:

                mot = new Client_motion();
                return mot.cnt_motion;

//			Random mot = new Random();
//			int Mot = mot.nextInt(2);
//
//			if(String.valueOf(Mot).equals("1"))
//				motion_state = 1;
//			else motion_state = 0;
//
//			return motion_state;

            //outlet device's state is randomly chosen on or off
            case 3:
                Random otlet = new Random();
                int Otlet = otlet.nextInt(2);

                if(String.valueOf(Otlet).equals("1"))
                    outlet_state = 1;
                else outlet_state = 0;

                return outlet_state;

            //bulb's state is based on motion state
            case 4:
                Random bul = new Random();
                int Bul = bul.nextInt(2);

                if(String.valueOf(Bul).equals("1"))
                    bulb_state = 1;
                else bulb_state = 0;

                return bulb_state;
            case 5:
                Random door = new Random();
                int Door = door.nextInt(2);

                if(String.valueOf(Door).equals("1"))
                    door_state = 1;
                else door_state = 0;

                return door_state;
        }

        return 0;
    }

//    public Long get_time() throws RemoteException {
//        return System.currentTimeMillis();
//    }

//    public void set_offset(Long a,Long b ,Long c,Long d) throws RemoteException{
//
//        outletOffset=a;
//        motionOffset=b;
//        bulbOffset=c;
//        tempOffset=d;
//
//
//    }

}
