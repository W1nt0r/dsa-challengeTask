package DomainObjects;

import java.io.Serializable;

public class State implements Serializable {

    private String ip;
    private int port;
    private boolean online;

    public final static State EMPTY_STATE = new State(null, 0, false);

    public State(String ip, int port, boolean online) {
        this.ip = ip;
        this.port = port;
        this.online = online;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "Peer: " +ip + ":" + port + ", Online: " + online;
    }
}
