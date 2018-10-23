package DomainObjects;

import DomainObjects.Interfaces.ITransmittable;
import DomainObjects.Interfaces.IMessageListener;

import java.io.Serializable;

public class ContactRequest implements Serializable, ITransmittable {

    private final Contact sender;

    public ContactRequest(Contact sender) {
        this.sender = sender;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveContactRequest(sender);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {
        listener.receiveContactRequestConfirmation(receiver);
    }
}
