package com.inter.client;

import java.rmi.RemoteException;

public interface ClientRemoteSens extends java.rmi.Remote {

    public int query_state(int id) throws RemoteException;
//    public Long get_time() throws RemoteException;
//    public void set_offset(Long a,Long b ,Long c,Long d) throws RemoteException;

}
