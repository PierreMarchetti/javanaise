package burst;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import irc.JvnProxy;
import irc.Sentence;
import irc.SentenceItf;
import jvn.JvnObject;
import jvn.JvnServerImpl;

public class BurstTestThread extends Thread {
	
	String name;
	
	public BurstTestThread(String name) {
		this.name = name;
	}

	public void run() {
		try {
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			
	        JvnObject jo = js.jvnLookupObject("IRC");

	        if (jo == null) {
	            jo = js.jvnCreateObject((Serializable) new Sentence());
	            // after creation, I have a write lock on the object
	            jo.jvnUnLock();
	            js.jvnRegisterObject("IRC", jo);
	            
	        }			
			for(int i=0;i<5000;i++) {
				jo.jvnLockWrite();
				Sentence s = (Sentence) jo.jvnGetSharedObject();
				s.write("floubi "+i);
				System.out.println("Client "+name+" : écriture "+i+" finie");
				jo.jvnUnLock();
				
			}
		} catch (Exception e) {
			System.err.println("Error on client "+name);
			e.printStackTrace();
		}
	}
}
