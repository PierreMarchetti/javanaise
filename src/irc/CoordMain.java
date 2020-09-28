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
            JvnRemoteCoord h_stub = (JvnRemoteCoord) UnicastRemoteObject.exportObject(jc,0);

            // Register the remote object in RMI registry with a given identifier
            Registry registry= LocateRegistry.getRegistry();
            registry.rebind("Service", h_stub);

            Registry registryClients= LocateRegistry.getRegistry("localhost",3500);

            System.out.println("Connexion Coord");

        } catch (Exception e) {
            System.out.println("IRC problem : " + e.getMessage());
        }
    }
}
