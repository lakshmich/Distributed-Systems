/**
 * Created by sahitya on 5/4/2015.
 */
package inter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TestRemote extends Remote {
    int register(String type, String name) throws RemoteException, NotBoundException, IOException;
    void change_mode(String mode) throws RemoteException;
    void report_state_by_sensor(int id, int state, Long logicTime) throws RemoteException;
    void heartbeat(int status) throws RemoteException;
    public void heartbeat_replica(int status) throws RemoteException;
}
