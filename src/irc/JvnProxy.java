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

	private JvnProxy(Class c, String jon) throws JvnException{
        JvnServerImpl js = JvnServerImpl.jvnGetServer();

        // look up the IRC object in the JVN server
        // if not found, create it, and register it in the JVN server
        jo = js.jvnLookupObject(jon);

        if (jo == null && c instanceof Serializable) {
        	try {
				jo = js.jvnCreateObject((Serializable)c.newInstance());
				// after creation, I have a write lock on the object
				jo.jvnUnLock();
				js.jvnRegisterObject(jon, jo);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	
	}

	public static Object newInstance(Class c, String jon) throws IllegalArgumentException, JvnException {
		return java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), c.getInterfaces(),
				new JvnProxy(c,jon));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Serializable s;
		if(method.isAnnotationPresent(SentenceMethodLockType.class)) {
			SentenceMethodLockType lockType = method.getAnnotation(SentenceMethodLockType.class);
			//System.out.println("annotation: "+lockType.name());
			
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
