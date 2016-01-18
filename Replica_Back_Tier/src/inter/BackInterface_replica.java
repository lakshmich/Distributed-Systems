package inter;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by sahitya on 3/31/2015.
 */
public interface BackInterface_replica extends Remote{
    public void newEntry(int id, int state, Long logicTime) throws RemoteException;
}
