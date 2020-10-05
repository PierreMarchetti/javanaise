package irc;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnServerImpl;

public class JvnProxy implements InvocationHandler {

	private Object object;
	private JvnObject jo;

	private JvnProxy() throws JvnException{
        JvnServerImpl js = JvnServerImpl.jvnGetServer();

        // look up the IRC object in the JVN server
        // if not found, create it, and register it in the JVN server
        jo = js.jvnLookupObject("IRC");

        if (jo == null) {
            jo = js.jvnCreateObject((Serializable) new Sentence());
            // after creation, I have a write lock on the object
            jo.jvnUnLock();
            js.jvnRegisterObject("IRC", jo);
        }
	
	
	}

	public static Object newInstance(Object o) throws IllegalArgumentException, JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(),
				new JvnProxy());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("proxy");
		Serializable s;
		if(method.isAnnotationPresent(SentenceMethodLockType.class)) {
			SentenceMethodLockType lockType = method.getAnnotation(SentenceMethodLockType.class);
			System.out.println("annotation: "+lockType.name());
			
			switch (lockType.name()) {
			case "write":
				jo.jvnLockWrite();
				s = jo.jvnGetSharedObject();
				method.invoke(s,args);
				jo.jvnUnLock();
				break;
			case "read":
				jo.jvnLockRead();
				s = jo.jvnGetSharedObject();
				Object o = method.invoke(s, args);
	
				jo.jvnUnLock();
				return o;
			default:
				break;
			}
		}
		
		
		//Object result = method.invoke(proxy, args); 
		
		return null;
	}

}
