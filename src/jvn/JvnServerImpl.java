/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;


public class JvnServerImpl
        extends UnicastRemoteObject
        implements JvnLocalServer, JvnRemoteServer {

    /**
     *
     */
    final int NB_COPY_LOCAL_MAX = 3;

    private static final long serialVersionUID = 1L;
    // A JVN server is managed as a singleton
    private static JvnServerImpl js = null;

    private JvnRemoteCoord coord;
    private List<JvnObject> jvnObjectList;

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnServerImpl() throws Exception {
        super();
        // to be completed
        Registry registry = LocateRegistry.getRegistry("localhost",2001);
        coord = (JvnRemoteCoord) registry.lookup("IRC");
        jvnObjectList = Collections.synchronizedList(new LinkedList<>());
        System.out.println("Connexion Server");
    }

    /**
     * Static method allowing an application to get a reference to
     * a JVN server instance
     *
     * @throws JvnException
     **/
    public static JvnServerImpl jvnGetServer() {
        if (js == null) {
            try {
                js = new JvnServerImpl();
            } catch (Exception e) {
                return null;
            }
        }
        return js;
    }

    /**
     * The JVN service is not used anymore
     *
     * @throws JvnException
     **/
    public void jvnTerminate()
            throws jvn.JvnException {
        try {
            coord.jvnTerminate(this);
        }catch (RemoteException e){
            throw new JvnException(e.getMessage());
        }
    }

    /**
     * creation of a JVN object
     *
     * @param o : the JVN object state
     * @throws JvnException
     **/
    public JvnObject jvnCreateObject(Serializable o)
            throws jvn.JvnException {
        try {
        	JvnObject jo = new JvnObjectImpl(coord.jvnGetObjectId(), o, JvnLockState.WLT);


            if (jvnObjectList.size()>=NB_COPY_LOCAL_MAX){
            	System.out.println("Limite de cache atteinte lors de la création de l'objet");
                JvnObjectImpl jvmObjImpl;
                boolean isDelete = false;
                for (JvnObject jvnObject : jvnObjectList){
                    jvmObjImpl = (JvnObjectImpl)jvnObject;
                    if(jvmObjImpl.state==JvnLockState.NL){
                        jvnObjectList.remove(jvnObject);
                        isDelete=true;
                        System.out.println("suppression du cache en verrou NL de "+jvnObject);
                        break;
                    }
                }
                if(!isDelete) {
                    for (JvnObject jvnObject : jvnObjectList) {
                        jvmObjImpl = (JvnObjectImpl) jvnObject;
                        if (jvmObjImpl.state == JvnLockState.RLC) {
                            jvnObjectList.remove(jvnObject);
                            System.out.println("suppression du cache en verrou RLC de "+jvnObject);
                            break;
                        }
                    }
                }
            }
            if(jvnObjectList.size()<NB_COPY_LOCAL_MAX) {
                jvnObjectList.add(jo);
            }else{
                System.out.println("Limite de copy local atteinte");
            }


            return jo;
        }catch (RemoteException e){
            throw new JvnException(e.getMessage());
        }
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @throws JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo)
            throws jvn.JvnException {
        try {
            coord.jvnRegisterObject(jon,jo,this);
        }catch (RemoteException e){
            throw new JvnException(e.getMessage());
        }
    }

    /**
     * Provide the reference of a JVN object beeing given its symbolic name
     *
     * @param jon : the JVN object name
     * @return the JVN object
     * @throws JvnException
     **/
    public JvnObject jvnLookupObject(String jon)
            throws jvn.JvnException {
        try {
        	JvnObject jo = coord.jvnLookupObject(jon,this);
        	if(jo!=null) {

                if (jvnObjectList.size()>=NB_COPY_LOCAL_MAX){
                	System.out.println("Limite de cache atteinte lors du lookup de l'objet "+jon);
                    JvnObjectImpl jvmObjImpl;
                    boolean isDelete = false;
                    for (JvnObject jvnObject : jvnObjectList){
                        jvmObjImpl = (JvnObjectImpl)jvnObject;
                        if(jvmObjImpl.state==JvnLockState.NL){
                            jvnObjectList.remove(jvnObject);
                            System.out.println("suppression du cache en verrou NL de "+jvnObject);
                            isDelete=true;
                            break;
                        }
                    }
                    if(!isDelete) {
                        for (JvnObject jvnObject : jvnObjectList) {
                            jvmObjImpl = (JvnObjectImpl) jvnObject;
                            if (jvmObjImpl.state == JvnLockState.RLC) {
                                System.out.println("suppression du cache en verrou RLC de "+jvnObject);
                                jvnObjectList.remove(jvnObject);
                                break;
                            }
                        }
                    }
                }
                if(jvnObjectList.size()<NB_COPY_LOCAL_MAX) {
                    jvnObjectList.add(jo);
                }else{
                    System.out.println("Saturation du cache");
                }

            }
            return jo;
        }catch (RemoteException e){
            throw new JvnException(e.getMessage());
        }
    }

    /**
     * Get a Read lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockRead(int joi)
            throws JvnException {
        try {
        	System.out.println("demande de lock read dans serveur");
			return coord.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			throw new JvnException(e.getMessage());
		}

    }

    /**
     * Get a Write lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockWrite(int joi)
            throws JvnException {
        try {
			return coord.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			throw new JvnException(e.getMessage());
		}
    }


    /**
     * Invalidate the Read lock of the JVN object identified by id
     * called by the JvnCoord
     *
     * @param joi : the JVN object id
     * @return void
     * @throws java.rmi.RemoteException,JvnException
     **/
    //rÃ©trograde read vers null
    public void jvnInvalidateReader(int joi)
            throws java.rmi.RemoteException, jvn.JvnException {

        JvnObject jo=null;
        for(JvnObject jvmObj : jvnObjectList){
            if (jvmObj.jvnGetObjectId()==joi){
                jo = jvmObj;
                break;
            }
        }
        if(jo!=null){
            jo.jvnInvalidateReader();
        }
    }

    ;

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriter(int joi)
            throws java.rmi.RemoteException, jvn.JvnException {

        JvnObject jo=null;
        for(JvnObject jvmObj : jvnObjectList){
            if (jvmObj.jvnGetObjectId()==joi){
                jo = jvmObj;
                break;
            }
        }
        if(jo==null){
            return null;
        }else{
            return jo.jvnInvalidateWriter();
        }
    }

    ;

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriterForReader(int joi)
            throws java.rmi.RemoteException, jvn.JvnException {


        JvnObject jo=null;
        for(JvnObject jvmObj : jvnObjectList){
            if (jvmObj.jvnGetObjectId()==joi){
                jo = jvmObj;
                break;
            }
        }
        if(jo==null){
            return null;
        }else{
            return jo.jvnInvalidateWriterForReader();
        }
    };

}

 
