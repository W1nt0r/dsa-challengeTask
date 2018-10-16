package Service;

import java.io.IOException;
import java.net.ServerSocket;

public class PortFinder {
    public static int findFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        return socket.getLocalPort();
    }
}
