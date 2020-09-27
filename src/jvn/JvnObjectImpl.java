package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
    int id;
    Serializable object;

    public JvnObjectImpl(int id, Serializable object) {
        this.id = id;
        this.object = object;
    }

    @Override
    public void jvnLockRead() throws JvnException {

    }

    @Override
    public void jvnLockWrite() throws JvnException {

    }

    @Override
    public void jvnUnLock() throws JvnException {

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
    public void jvnInvalidateReader() throws JvnException {

    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }
}
