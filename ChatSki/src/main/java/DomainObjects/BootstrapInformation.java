package DomainObjects;

import java.io.Serializable;

public class BootstrapInformation implements Serializable {

    private String ipAddress;
    private int port;

    public BootstrapInformation(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}