/**
 * Created by sahitya on 5/4/2015.
 */
package com.client;

import com.inter.client.ClientRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client_Impl_dev extends UnicastRemoteObject implements ClientRemote{

    private static final long serialVersionUID = 1L;
    int outlet_state;
    int bulb_state;
    int motion_state;
    Long outletOffset=0L;
    Long motionOffset=0L;
    Long bulbOffset=0L;
    Long tempOffset=0L;

    protected Client_Impl_dev() throws RemoteException {
        super();
        // TODO Auto-generated constructor stubz
    }

    //change_state method defined for bulb and outlet devices
    public int change_state(int id, int state) throws RemoteException {

        switch(id){
            case 3:
                if (state==0)
                    return outlet_state = state;

                else if(state==1)
                    return outlet_state = state;

            case 4:
                if (state==0)
                    return bulb_state = state;

                else if(state==1)
                    return bulb_state = state;
        }
        return 0;
    }

//    public Long get_time() throws RemoteException{
//
//        return System.currentTimeMillis();
//    }

//    public void set_offset(Long a,Long b ,Long c,Long d) throws RemoteException{
//
//        outletOffset=a;
//        motionOffset=b;
//        bulbOffset=c;
//        tempOffset=d;
//
//    }
}

