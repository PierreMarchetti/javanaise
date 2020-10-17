/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class JvnCoordImpl
        extends UnicastRemoteObject
        implements JvnRemoteCoord {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    ConcurrentHashMap<String, Integer> listNameJvnServer;
    ConcurrentHashMap<Integer, List<JvnServerState>> jvnRemoteServerMap;
    private static JvnCoordImpl jc = null;
    

    private int lastNumId;
    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        jvnRemoteServerMap = new ConcurrentHashMap<>();
        listNameJvnServer = new ConcurrentHashMap<>();
    }

    public static synchronized JvnCoordImpl getInstance() {
        if (jc == null) {
            try {
                jc = new JvnCoordImpl();
            } catch (Exception e) {
                return null;
            }
        }
        return jc;
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a
     * newly created JVN object)
     *
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized int jvnGetObjectId()
            throws java.rmi.RemoteException, jvn.JvnException {
        int i = lastNumId;
        lastNumId++;
        return i;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException, jvn.JvnException {
        if(jvnRemoteServerMap.get(jo.jvnGetObjectId())==null) {
            jvnRemoteServerMap.put(jo.jvnGetObjectId(), new ArrayList<>());
        }
        if (jvnRemoteServerMap.get(jo.jvnGetObjectId()).stream().noneMatch((jss -> jss.getJvnRemoteServer().equals(js)))) {
            jvnRemoteServerMap.get(jo.jvnGetObjectId()).add(new JvnServerState(js,JvnCoordLockState.W));
        }else{
            System.out.println("obj déjà dedans : "+jon);
        }
        listNameJvnServer.put(jon,jo.jvnGetObjectId());
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
            throws java.rmi.RemoteException, jvn.JvnException {
        try {
            Integer joi = listNameJvnServer.get(jon);
            if (jvnRemoteServerMap.get(joi).stream().noneMatch((jss -> jss.getJvnRemoteServer().equals(js)))) {
                jvnRemoteServerMap.get(joi).add(new JvnServerState(js,JvnCoordLockState.NL));
            }else{
                System.out.println("obj déjà dedans : "+jon);
            }
            
            return new JvnObjectImpl(joi,null);
        }catch (NullPointerException npe){
            return null;
        }
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {

        List<JvnServerState> jvnRemoteServerList = this.jvnRemoteServerMap.get(joi);


        for (JvnServerState jvnServerState_tmp : jvnRemoteServerList) {
	        switch (jvnServerState_tmp.getState()){
	            case W:
	            	synchronized (this) {
		                Serializable obj = jvnServerState_tmp.getJvnRemoteServer().jvnInvalidateWriterForReader(joi);
	                    putStateToServer(joi,js,JvnCoordLockState.R);
		                return obj;
					}

			default:
				break;
	        
        	}
        }

        return null;

    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {

        List<JvnServerState> jvnRemoteServerList = this.jvnRemoteServerMap.get(joi);

        Serializable obj=null;
        
        for (JvnServerState jvnServerState_tmp : jvnRemoteServerList) {
            switch (jvnServerState_tmp.getState()){
            case W:
                obj = jvnServerState_tmp.getJvnRemoteServer().jvnInvalidateWriter(joi);
                jvnServerState_tmp.setState(JvnCoordLockState.NL);
                break;
            case R:
                jvnServerState_tmp.getJvnRemoteServer().jvnInvalidateReader(joi);
                jvnServerState_tmp.setState(JvnCoordLockState.NL);
                break;
			default:
				break;
            }
        	

        }
        putStateToServer(joi,js,JvnCoordLockState.W);

        return obj;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        jvnRemoteServerMap.remove(js);
        
        for (List<JvnServerState> jssList : jvnRemoteServerMap.values()) {
			jssList.removeIf(jss -> jss.getJvnRemoteServer().equals(js));
		}
    }


    private void putStateToServer(int joi, JvnRemoteServer js,JvnCoordLockState state){
        List<JvnServerState> jvnRemoteServerList = this.jvnRemoteServerMap.get(joi);
        boolean jsAlreadyExists = false;
        for (JvnServerState jServState : jvnRemoteServerList){
            if(jServState.getJvnRemoteServer().equals(js)){
                jServState.setState(state);
                jsAlreadyExists = true;
                break;
            }
        }
        if (!jsAlreadyExists){
            this.jvnRemoteServerMap.get(joi).add(new JvnServerState(js,state));
        }
    }
}

 
