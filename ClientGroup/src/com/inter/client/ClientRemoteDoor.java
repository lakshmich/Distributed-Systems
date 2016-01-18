package com.inter.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by sahitya on 4/2/2015.
 */
public interface ClientRemoteDoor extends Remote{
    public int query_state_door(int id) throws RemoteException;

}
