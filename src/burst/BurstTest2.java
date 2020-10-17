package burst;

import java.io.InputStream;
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

public class BurstTest2 {
	public static void main(String[] args) {
		
		try {
			JvnServerImpl js = JvnServerImpl.jvnGetServer();			
	        JvnObject jo = js.jvnLookupObject("IRC");

	        if (jo == null) {
	            jo = js.jvnCreateObject((Serializable) new Sentence());
	            jo.jvnUnLock();
	            js.jvnRegisterObject("IRC", jo);           
	        }
	        
			for(int i=0;i<5000;i++) {
				jo.jvnLockRead();
				Sentence s = (Sentence) jo.jvnGetSharedObject();
				System.out.println("Lecture "+i+" de "+s.read());
				jo.jvnUnLock();
			}
		} catch (Exception e) {
			System.err.println("Error on main ");
			e.printStackTrace();
		}
	}	
}
