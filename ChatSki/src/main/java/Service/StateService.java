package Service;

import DomainObjects.State;
import Service.Exceptions.PeerNotInitializedException;
import Service.Exceptions.ReplicationException;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.PutBuilder;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.function.Consumer;


public class StateService {

    private final static String STATE_KEY_PREFIX = "state-";
    private final static Object replicationLock = new Object();

    private static ReplicationService currentReplication =
            new NullReplicationService(StateService::changeReplication);

    public static void SaveStateToDht(String stateId, String username,
                                      State stateToSave,
                                      Consumer<String> onFinishReplication) throws PeerNotInitializedException, ReplicationException {
        final String stateKey = STATE_KEY_PREFIX + username;
        PeerDHT ownPeer = PeerHolder.getOwnPeer();

        try {
            PutBuilder putBuilder = ownPeer.put(Number160.createHash(stateKey)).data(new Data(stateToSave));

            synchronized (replicationLock) {
                ReplicationService newReplicationService =
                        new ReplicationService(stateId,
                                ownPeer.peer(),
                                putBuilder, StateService::changeReplication, onFinishReplication);
                currentReplication.shutdown(newReplicationService);
            }
        } catch (IOException e) {
            throw new ReplicationException("Could not make replications");
        }

    }

    private static synchronized void changeReplication(
            ReplicationService nextReplication) {
        synchronized (replicationLock) {
            currentReplication = nextReplication;
        }
    }

    public static void LoadStateFromDht(String username,
                                        Consumer<State> onLoadState,
                                        Consumer<Throwable> onError) throws PeerNotInitializedException {
        final String stateKey = STATE_KEY_PREFIX + username;
        PeerDHT ownPeer = PeerHolder.getOwnPeer();
        FutureGet future = ownPeer.get(Number160.createHash(stateKey)).start();
        future.addListener(new BaseFutureListener<FutureGet>() {
            @Override
            public void operationComplete(FutureGet future)
                    throws IOException, ClassNotFoundException {
                Data data = future.data();
                if (data == null) {
                    onLoadState.accept(null);
                } else {
                    onLoadState.accept((State) future.data().object());
                }
            }

            @Override
            public void exceptionCaught(Throwable t) {
                onError.accept(t);
            }
        });
    }
}
