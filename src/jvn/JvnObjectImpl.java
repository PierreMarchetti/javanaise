package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
    int id;
    Serializable object;
    JvnLockState state;

    public JvnObjectImpl(int id, Serializable object) {
        this.id = id;
        this.object = object;
        this.state = JvnLockState.NL;
    }
    
    public JvnObjectImpl(int id, Serializable object, JvnLockState state) {
        this.id = id;
        this.object = object;
        this.state = state;
    }

    @Override
    public void jvnLockRead() throws JvnException {
    	switch (state) {
		case WLC:
			state = JvnLockState.RLT_WLC;
			break;
		case RLC: 
			state = JvnLockState.RLT;
			break;
		case RLT:
		case WLT:
		case RLT_WLC:
			throw new JvnException("Lock already taken");
		default:
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
	    	object = js.jvnLockRead(id);
	    	state = JvnLockState.RLT;
			break;
		}
    	
    	
    }

    @Override
    public void jvnLockWrite() throws JvnException {
    	switch (state) {
		case WLT:
			throw new JvnException("Lock already taken");
		case WLC:
		default:
	    	JvnServerImpl js = JvnServerImpl.jvnGetServer();
	    	object = js.jvnLockWrite(id);
	    	state = JvnLockState.WLT;
			break;
		}

    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
    	JvnServerImpl js = JvnServerImpl.jvnGetServer();
    	switch (state) {
		case WLT:
		case RLT_WLC:
			state = JvnLockState.WLC;
			break;
		case RLT:
			state = JvnLockState.RLC;
			break;
		default:
			break;
		}
    	notify();
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return object;
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
    	try {
    		while(state == JvnLockState.RLT || state == JvnLockState.RLT_WLC) {
    			wait();
    		}
			state = JvnLockState.NL;

		} catch (InterruptedException e) {
			throw new JvnException(e.getMessage());
		}
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
    	try {
    		while(state == JvnLockState.WLT) {
    			wait();
    			
    		}
    		state = JvnLockState.NL;
			return object;
		} catch (InterruptedException e) {
			throw new JvnException(e.getMessage());
		}
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
    	try {
    		while(state == JvnLockState.WLT) {
    			wait();
    		}
			state = JvnLockState.RLC;

			return object;
		} catch (InterruptedException e) {
			throw new JvnException(e.getMessage());
		}
    }
}
