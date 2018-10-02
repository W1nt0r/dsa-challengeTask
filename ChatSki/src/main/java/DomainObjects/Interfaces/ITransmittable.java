package DomainObjects.Interfaces;

public interface ITransmittable {
    void handleReception(IMessageListener listener);
}
