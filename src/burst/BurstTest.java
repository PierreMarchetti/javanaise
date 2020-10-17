package burst;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

import irc.JvnProxy;
import irc.Sentence;
import irc.SentenceItf;
import jvn.JvnObject;
import jvn.JvnObjectImpl;
import jvn.JvnServerImpl;

public class BurstTest {
	public synchronized static void main(String[] args) {
		BurstTestThread b1 = new BurstTestThread("a");
//		BurstTestThread b2 = new BurstTestThread("b");
		
		try {
			b1.start();
//			b2.start();
		} catch (Exception e) {
			System.err.println("Error on main ");
			e.printStackTrace();
		}
	}	
}
