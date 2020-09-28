package irc;

import jvn.JvnCoordImpl;
import jvn.JvnRemoteCoord;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CoordMain {
    public static void main(String argv[]) {
        try {
            // initialize coord
            JvnCoordImpl jc = JvnCoordImpl.getInstance();
           

            // Register the remote object in RMI registry with a given identifier
            Registry registry= LocateRegistry.getRegistry("127.0.0.1",2001);
            registry.rebind("IRC", jc);

            System.out.println("Connexion Coord");

        } catch (Exception e) {
            System.out.println("IRC problem : " + e.getMessage());
        }
    }
}
