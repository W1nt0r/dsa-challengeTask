package DomainObjects.Interfaces;

public interface IPeerListener {
    void peerClosed();

    void showThrowable(Throwable throwable);
}
