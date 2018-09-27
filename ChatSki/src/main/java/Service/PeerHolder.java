package Service;

import net.tomp2p.dht.PeerDHT;

import java.io.IOException;

public class PeerHolder {

    private static PeerDHT ownPeer;

    public static void initializePeer(String id, int port) throws IOException {
        PeerDHT peer = PeerCreator.CreatePeer(id, port);
        Bootstrap.bootstrap(peer);

        ownPeer = peer;
    }

    static PeerDHT getOwnPeer() {
        return ownPeer;
    }
}
