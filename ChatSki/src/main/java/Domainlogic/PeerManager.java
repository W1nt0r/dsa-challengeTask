package Domainlogic;

import DomainObjects.BootstrapInformation;
import DomainObjects.Interfaces.IPeerListener;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Service.PeerHolder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class PeerManager {
    public static String initializePeer(String id,
                                        int port) throws PeerCreateException {
        try {
            return PeerHolder.initializePeer(id, port);
        } catch (IOException ex) {
            throw new PeerCreateException(ex);
        }
    }

    public static boolean bootstrap(
            BootstrapInformation bootstrapInfo) throws NetworkJoinException {
        try {
            return PeerHolder.bootstrap(bootstrapInfo);
        } catch (UnknownHostException e) {
            throw new NetworkJoinException(e);
        }
    }

    public static boolean isPeerInitialized() {
        return PeerHolder.isPeerInitialized();
    }

    public static void closePeer(IPeerListener peerListener) {
        PeerHolder.closePeer(peerListener::peerClosed, peerListener::showThrowable);
    }
}