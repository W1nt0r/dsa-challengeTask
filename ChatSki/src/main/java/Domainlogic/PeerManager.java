package Domainlogic;

import Domainlogic.Exceptions.NetworkJoinException;
import Service.PeerHolder;

import java.io.IOException;

public class PeerManager {
    public static void initializePeer(String id, int port) throws NetworkJoinException {
        try {
            PeerHolder.initializePeer(id, port);
        } catch (IOException ex) {
            throw new NetworkJoinException(ex);
        }
    }
}
