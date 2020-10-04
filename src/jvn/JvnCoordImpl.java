/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import irc.Irc;
import irc.Sentence;
import jdk.internal.util.xml.impl.Pair;

import javax.swing.plaf.nimbus.State;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JvnCoordImpl
        extends UnicastRemoteObject
        implements JvnRemoteCoord {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    ConcurrentHashMap<String, Integer> listNameJvnServer;
    ConcurrentHashMap<Integer, List<JvnRemoteServer>> jvnRemoteServerMap;
    ConcurrentHashMap<JvnRemoteServer,JvnLockState> jvnLockStateServ;
    private static JvnCoordImpl jc = null;
    

    private int lastNumId;//incrémenté à chaque sentence
    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        // to be completed
        jvnRemoteServerMap = new ConcurrentHashMap<>();
        listNameJvnServer = new ConcurrentHashMap<>();
        jvnLockStateServ = new ConcurrentHashMap<>();
    }

    public static JvnCoordImpl getInstance() {
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
    public int jvnGetObjectId()
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
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
            throws java.rmi.RemoteException, jvn.JvnException {
        // to be completed
        if(jvnRemoteServerMap.get(jo.jvnGetObjectId())==null) {
            jvnRemoteServerMap.put(jo.jvnGetObjectId(), new ArrayList<>());
        }
        if (!jvnRemoteServerMap.get(jo.jvnGetObjectId()).contains(js)) {
            jvnRemoteServerMap.get(jo.jvnGetObjectId()).add(js);
        }else{
            System.out.println("obj déja dedans : "+jon);
        }
        jvnLockStateServ.put(js,JvnLockState.WLT);
        listNameJvnServer.put(jon,jo.jvnGetObjectId());
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
            throws java.rmi.RemoteException, jvn.JvnException {
        // to be completed
        try {
            Integer joi = listNameJvnServer.get(jon);
            if (!jvnRemoteServerMap.get(joi).contains(js)) {
                jvnRemoteServerMap.get(joi).add(js);
            }
            jvnLockStateServ.put(js, JvnLockState.NL);
            
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

        List<JvnRemoteServer> jvnRemoteServerList = this.jvnRemoteServerMap.get(joi);



        for (JvnRemoteServer jvnRemoteServ_tmp : jvnRemoteServerList) {


	        switch (jvnLockStateServ.get(jvnRemoteServ_tmp)){
	            case WLC:
	            case WLT:
	            case RLT_WLC:
	                Serializable obj = jvnRemoteServ_tmp.jvnInvalidateWriterForReader(joi);
	                jvnLockStateServ.put(js,JvnLockState.RLT);
	                return obj;
	        
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
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
            throws java.rmi.RemoteException, JvnException {
        // to be completed


        List<JvnRemoteServer> jvnRemoteServerList = this.jvnRemoteServerMap.get(joi);

        Serializable obj=null;

        for (JvnRemoteServer jvnRemoteServ_tmp : jvnRemoteServerList) {
            switch (jvnLockStateServ.get(jvnRemoteServ_tmp)){
            case WLC:
            case WLT:
            case RLT_WLC:
                obj = jvnRemoteServ_tmp.jvnInvalidateWriter(joi);
                jvnLockStateServ.put(jvnRemoteServ_tmp,JvnLockState.NL);
                jvnLockStateServ.put(js,JvnLockState.WLT);
                break;
            case RLT:
            case RLC:
                jvnRemoteServ_tmp.jvnInvalidateReader(joi);
                jvnLockStateServ.put(jvnRemoteServ_tmp,JvnLockState.NL);
            }
        	

        }

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
        // to be completed
        jvnRemoteServerMap.remove(js);
    }
}

 
