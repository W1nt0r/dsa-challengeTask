package Service;

import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;

import java.io.IOException;

public final class PeerCreator {
    public static PeerDHT CreatePeer(String id, int port) throws IOException {
        PeerBuilder builder = new PeerBuilder(Number160.createHash(id));
        return new PeerBuilderDHT(builder.ports(port).start()).start();
    }
}
