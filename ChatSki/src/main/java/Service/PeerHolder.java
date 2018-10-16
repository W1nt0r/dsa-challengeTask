package Service;

import DomainObjects.BootstrapInformation;
import Service.Exceptions.PeerNotInitializedException;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;
import java.net.UnknownHostException;

public class PeerHolder {

    private static PeerDHT ownPeer;

    public static String initializePeer(String id, int port) throws IOException {
        ownPeer = PeerCreator.CreatePeer(id, port);
        return ownPeer.peerAddress().inetAddress().toString();
    }

    public static boolean bootstrap(BootstrapInformation bootstrapInfo) throws UnknownHostException {
        return Bootstrap.bootstrap(ownPeer, bootstrapInfo);
    }

    static PeerDHT getOwnPeer() throws PeerNotInitializedException{
        if(ownPeer == null) {
            throw new PeerNotInitializedException();
        }

        return ownPeer;
    }
}
