package jvn;

public class JvnServerState {
    JvnRemoteServer jvnRemoteServer;
    JvnLockState state;

    public JvnServerState(JvnRemoteServer jvnRemoteServer) {
        this.jvnRemoteServer = jvnRemoteServer;
        this.state = JvnLockState.NL;
    }

    public JvnServerState(JvnRemoteServer jvnRemoteServer, JvnLockState state) {
        this.jvnRemoteServer = jvnRemoteServer;
        this.state = state;
    }

    public JvnLockState getState() {
        return state;
    }

    public void setState(JvnLockState state) {
        this.state = state;
    }

    public JvnRemoteServer getJvnRemoteServer() {
        return jvnRemoteServer;
    }
}
