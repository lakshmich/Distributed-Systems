package com.client; /**
 * Created by sahitya on 5/4/2015.
 */


import com.inter.client.ClientRemoteDoor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by sahitya on 4/2/2015.
 */
public class Client_Impl_door extends UnicastRemoteObject implements ClientRemoteDoor {
    public int LC_door_impl;

    protected Client_Impl_door() throws RemoteException {
        super();
    }

    public int query_state_door(int id) throws RemoteException{
        com.client.Client_door dor = new com.client.Client_door();
        return dor.state;
    }
}

