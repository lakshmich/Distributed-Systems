import inter.BackInterface_replica;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import static java.nio.file.StandardCopyOption.*;
/**
 * Created by sahitya on 3/31/2015.
 */
public class DatabaseClass_replica extends UnicastRemoteObject implements BackInterface_replica {

    public static HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();


    class data_entry_replica{
        int id;
        int state;
        Long logicTime;
    }

    data_entry_replica data_obj = new data_entry_replica();
    Queue<data_entry_replica> cache_back = new LinkedList<data_entry_replica>();

    protected DatabaseClass_replica() throws RemoteException {
    }

    //method through which data is being entered into log file
    public void newEntry(int id, int state, Long logicTime) throws RemoteException{
        states.put(id, state);
        data_obj.id = id;
        data_obj.state = state;
        data_obj.logicTime = logicTime;

        if(cache_back.size() >=20 ){
            cache_back.remove();
            cache_back.add(data_obj);
        }
        else
            cache_back.add(data_obj);

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("datafile.txt", true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        java.util.Date today = new java.util.Date();
//        out.println(new java.sql.Timestamp(today.getTime())+ " " + id + " " + state + " " + logicTime);      //time - id of device or sensor - state of sensor or device - time at which information is sent from remote clients and server
//        out.close();

//        PrintWriter out_replica = null;
//        try {
//            out_replica = new PrintWriter(new BufferedWriter(new FileWriter("datafile_replicated.txt", true)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    {
        Path source = Paths.get(new File("").getAbsolutePath() + "\\datafile.txt");
        Path target = Paths.get(new File("").getAbsolutePath() + "\\datafile_replicated.txt");

        try {
            Files.copy(source, target, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {

        DatabaseClass_replica data_impl = new DatabaseClass_replica();
        Registry register = LocateRegistry.createRegistry(8000);      //creates a registry at port 8000 for remote clients and server to input data into log file
        register.bind("localhost", data_impl);

//        PrintWriter out = null;
//        try {
//            out = new PrintWriter(new BufferedWriter(new FileWriter("datafile_replicated.txt", true)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
