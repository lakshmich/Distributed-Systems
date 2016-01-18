package com.inter.client;

import java.rmi.RemoteException;

public interface ClientRemote extends java.rmi.Remote {


    public int change_state(int id, int state) throws RemoteException;
//    public Long get_time() throws RemoteException;
//    public void set_offset(Long a,Long b ,Long c,Long d) throws RemoteException;


}
