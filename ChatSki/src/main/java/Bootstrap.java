import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;

public class Bootstrap {

    private PeerDHT peer;

    public final static Number160 FILE_HASH = Number160.createHash("FIRST_FILE");

    public final static String BOOTSTRAP_ID = "BOOTSTRAP_PEER_ID";
    public final static int BOOTSTRAP_PORT = 4000;

    public Bootstrap() throws IOException {
        peer = PeerCreator.CreatePeer(BOOTSTRAP_ID, BOOTSTRAP_PORT);
        peer.peer().objectDataReply(new ObjectDataReply() {
            @Override
            public Object reply(PeerAddress sender, Object request) throws Exception {
                receiveMessage(request);
                return "This is a reply";
            }
        });
    }

    public PeerDHT getBootstrapPeer() {
        return peer;
    }

    private void receiveMessage(Object data) {
        System.out.println(data.toString());
    }

    public static void main(String[] args) throws IOException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.getBootstrapPeer().put(Number160.createHash("this is a test")).data(new Data("Hello, world!")).start().awaitUninterruptibly();
    }
}
