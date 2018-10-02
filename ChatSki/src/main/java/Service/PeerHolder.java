package Service;

import DomainObjects.BootstrapInformation;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;
import java.net.UnknownHostException;

public class PeerHolder {

    private static PeerDHT ownPeer;

    public static void initializePeer(String id, int port) throws IOException {
        ownPeer = PeerCreator.CreatePeer(id, port);
    }

    public static boolean bootstrap(BootstrapInformation bootstrapInfo) throws UnknownHostException {
        return Bootstrap.bootstrap(ownPeer, bootstrapInfo);
    }

    static PeerDHT getOwnPeer() {
        return ownPeer;
    }
}