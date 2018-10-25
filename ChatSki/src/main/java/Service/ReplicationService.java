package Service;

import Service.Enums.ReplicationState;
import net.tomp2p.dht.PutBuilder;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.p2p.JobScheduler;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.Shutdown;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ReplicationService {
    private static final int REPLICATION_INTERVAL = 1000;
    private static final int REPLICATION_REPETITIONS = -1;
    private static final int REPLICATION_WAIT_TIME = 9 * REPLICATION_INTERVAL;
    private String id;
    private Peer peer;
    private ReplicationState state;
    private Shutdown shutdown;
    private List<Consumer<String>> consumers;
    private ReplicationService nextReplicationService;
    private PutBuilder builder;

    protected Consumer<ReplicationService> changeService;

    public ReplicationService(String id, Peer peer, PutBuilder builder,
                              Consumer<ReplicationService> changeService,
                              Consumer<String> consumer) {
        this.id = id;
        this.peer = peer;
        this.builder = builder;
        this.changeService = changeService;
        consumers = new LinkedList<>();
        if (consumer != null) {
            consumers.add(consumer);
        }
        state = ReplicationState.WORKING;
    }

    public synchronized void startReplication() {
        JobScheduler replication = new JobScheduler(peer);
        shutdown = replication.start(builder, REPLICATION_INTERVAL, REPLICATION_REPETITIONS,
                (future -> System.out.println("added replication " + id)));

        new Thread(() -> {
            try {
                Thread.sleep(REPLICATION_WAIT_TIME);
                shutdown(nextReplicationService);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public synchronized void shutdown(
            ReplicationService nextReplicationService) {
        if (state == ReplicationState.WORKING) {
            state = ReplicationState.SHUTTING_DOWN;
            this.nextReplicationService = nextReplicationService;
            shutdown();
        } else if (state == ReplicationState.SHUTTING_DOWN) {
            consumers.addAll(this.nextReplicationService.consumers);
            this.nextReplicationService = nextReplicationService;
        }
    }

    private synchronized void shutdown() {
        BaseFuture future = shutdown.shutdown();
        future.addListener(new BaseFutureListener<BaseFuture>() {
            @Override
            public void operationComplete(BaseFuture future) {
                shutdownFinished();
            }

            @Override
            public void exceptionCaught(Throwable t) {

            }
        });
    }

    private synchronized void shutdownFinished() {
        state = ReplicationState.SHUTDOWN;
        for (Consumer<String> consumer : consumers) {
            consumer.accept(id);
        }
        consumers.clear();
        if (nextReplicationService == null) {
            nextReplicationService = new NullReplicationService(changeService);
        }
        changeService.accept(nextReplicationService);
        nextReplicationService.startReplication();
    }
}
