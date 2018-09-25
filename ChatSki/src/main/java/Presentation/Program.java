package Presentation;

import Service.PeerCreator;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.io.IOException;
import java.net.InetAddress;

public class Program {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        PeerDHT peer = PeerCreator.CreatePeer("OTHER_PEER", 4001);
        FutureBootstrap fb = peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(Bootstrap.BOOTSTRAP_PORT).start();
        fb.awaitUninterruptibly();
        System.out.println(fb.isSuccess());

        /*if(fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }*/

        FutureDirect fd = peer.peer().sendDirect(new PeerAddress(Number160.createHash(Bootstrap.BOOTSTRAP_ID), InetAddress.getByName("127.0.0.1"), 4000, 4000)).object("This is a direct message").start();
        fd.awaitUninterruptibly();
        if (fd.isSuccess()) {
            System.out.println(fd.object().toString());
        }

        System.out.println(peer.peerAddress().inetAddress());

        FutureGet futureGet = peer.get(Number160.createHash("fabian")).start();
        futureGet.awaitUninterruptibly();
        System.out.println(futureGet.data().object());
    }
}