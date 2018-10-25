package Service;

import DomainObjects.BootstrapInformation;
import Service.Exceptions.PeerNotInitializedException;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class PeerHolder {

    private static PeerDHT ownPeer;

    public static String initializePeer(String id, int port) throws IOException {
        ownPeer = PeerCreator.CreatePeer(id, port);
        return ownPeer.peerAddress().inetAddress().toString();
    }

    public static boolean bootstrap(BootstrapInformation bootstrapInfo) throws UnknownHostException {
        return Bootstrap.bootstrap(ownPeer, bootstrapInfo);
    }

    public static void closePeer(Runnable onClosed, Consumer<Throwable> onError) {
        if (ownPeer != null) {
            BaseFuture future = ownPeer.shutdown();
            future.addListener(new BaseFutureListener<BaseFuture>() {
                @Override
                public void operationComplete(
                        BaseFuture future) {
                    onClosed.run();
                }

                @Override
                public void exceptionCaught(Throwable t) {
                    onError.accept(t);
                }
            });
        }
    }

    public static boolean isPeerInitialized() {
        return ownPeer != null;
    }

    static PeerDHT getOwnPeer() throws PeerNotInitializedException{
        if(ownPeer == null) {
            throw new PeerNotInitializedException();
        }

        return ownPeer;
    }
}
