package Service;

import DomainObjects.State;
import Service.Exceptions.PeerNotAvailableException;
import Service.Exceptions.PeerNotInitializedException;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.PutBuilder;
import net.tomp2p.p2p.JobScheduler;
import net.tomp2p.p2p.Shutdown;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;


public class StateService {

    private final static String STATE_KEY_PREFIX = "state-";
    private final static int REPLICATION_WAIT_TIME = 9 * 1000;

    public static boolean SaveStateToDht(String username, State stateToSave) throws PeerNotInitializedException {
        try {
            final String stateKey = STATE_KEY_PREFIX + username;
            PeerDHT ownPeer = PeerHolder.getOwnPeer();

            PutBuilder putBuilder = ownPeer.put(Number160.createHash(stateKey)).data(new Data(stateToSave));

            new Thread(() -> {
                try {
                    JobScheduler replication = new JobScheduler(ownPeer.peer());
                    Shutdown shutdown = replication.start(putBuilder, 1000, -1, (future) ->
                            System.out.println("added replication"));

                    Thread.sleep(REPLICATION_WAIT_TIME);
                    System.out.println("stop replication");
                    shutdown.shutdown();
                } catch (InterruptedException ignored) {
                }

            }).start();

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static State LoadStateFromDht(String username) throws PeerNotAvailableException, PeerNotInitializedException {
        final String stateKey = STATE_KEY_PREFIX + username;
        PeerDHT ownPeer = PeerHolder.getOwnPeer();

        try {
            return (State) ownPeer.get(Number160.createHash(stateKey)).start()
                    .awaitUninterruptibly().data().object();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            throw new PeerNotAvailableException();
        }
    }
}
