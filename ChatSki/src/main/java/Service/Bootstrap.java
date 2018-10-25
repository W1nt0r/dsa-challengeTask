package Service;

import DomainObjects.BootstrapInformation;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Bootstrap {

    public static boolean bootstrap(PeerDHT peer, BootstrapInformation bootstrapInfo) throws UnknownHostException {
        FutureBootstrap fb = peer.peer().bootstrap().inetAddress(InetAddress.getByName(bootstrapInfo.getIpAddress())).ports(bootstrapInfo.getPort()).start();
        fb.awaitUninterruptibly();
        return fb.isSuccess();
    }

    public static void main(String[] args) throws IOException {
        final String bootstrapId = "BOOTSTRAP_PEER_ID";
        final int bootstrapPort = 4000;
        PeerDHT bootstrapNode = PeerCreator.CreatePeer(bootstrapId,
                bootstrapPort);
        System.out.println("IP-Address: " + bootstrapNode.peerAddress().inetAddress());
        System.out.println("Port: " + bootstrapPort);
    }
}