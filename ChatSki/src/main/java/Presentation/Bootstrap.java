package Presentation;

import Service.PeerCreator;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Bootstrap {

    private final static String BOOTSTRAP_ID = "BOOTSTRAP_PEER_ID";
    private final static int BOOTSTRAP_PORT = 4000;
    private final static String BOOTSTRAP_IP = "127.0.0.1";

    public static boolean bootstrap(PeerDHT peer) throws UnknownHostException {
        FutureBootstrap fb = peer.peer().bootstrap().inetAddress(InetAddress.getByName(Bootstrap.BOOTSTRAP_IP)).ports(Bootstrap.BOOTSTRAP_PORT).start();
        fb.awaitUninterruptibly();
        return fb.isSuccess();
    }

    public static void main(String[] args) throws IOException {
        PeerDHT peer = PeerCreator.CreatePeer(BOOTSTRAP_ID, BOOTSTRAP_PORT);
        System.out.println(peer.put(Number160.createHash("TestFile")).data(new Data("Hello, world")).start().awaitUninterruptibly().isSuccess());
    }
}