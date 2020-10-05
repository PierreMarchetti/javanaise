package jvn;

public class JvnServerState {
    JvnRemoteServer jvnRemoteServer;
    JvnCoordLockState state;

    public JvnServerState(JvnRemoteServer jvnRemoteServer) {
        this.jvnRemoteServer = jvnRemoteServer;
        this.state = JvnCoordLockState.NL;
    }

    public JvnServerState(JvnRemoteServer jvnRemoteServer, JvnCoordLockState state) {
        this.jvnRemoteServer = jvnRemoteServer;
        this.state = state;
    }

    public JvnCoordLockState getState() {
        return state;
    }

    public void setState(JvnCoordLockState state) {
        this.state = state;
    }

    public JvnRemoteServer getJvnRemoteServer() {
        return jvnRemoteServer;
    }
}
