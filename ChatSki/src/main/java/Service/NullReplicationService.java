package Service;

import java.util.function.Consumer;

public class NullReplicationService extends ReplicationService {
    public NullReplicationService(Consumer<ReplicationService> changeService) {
        super(null, null, null, changeService, null);
    }

    @Override
    public synchronized void startReplication() {}

    @Override
    public synchronized void shutdown(
            ReplicationService nextReplicationService) {
        changeService.accept(nextReplicationService);
        nextReplicationService.startReplication();
    }
}
