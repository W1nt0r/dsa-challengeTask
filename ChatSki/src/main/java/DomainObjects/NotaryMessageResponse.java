package DomainObjects;

import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class NotaryMessageResponse implements Serializable, ITransmittable {

    private Contact sender;
    private NotaryMessage message;

    public NotaryMessageResponse(Contact sender, NotaryMessage message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveNotaryMessageResponse(sender, message);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {

    }
}
