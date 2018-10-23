package DomainObjects.Interfaces;

import DomainObjects.Contact;

public interface ITransmittable {
    void handleReception(IMessageListener listener);
    void handleConfirmation(Contact receiver, IMessageListener listener);
}
