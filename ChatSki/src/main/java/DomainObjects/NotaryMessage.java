package DomainObjects;

import DomainObjects.Interfaces.IMessage;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class NotaryMessage implements Serializable, ITransmittable, IMessage {

    private Contact sender;
    private String message;

    public NotaryMessage(Contact sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public Contact getSender() {
        return sender;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveNotaryMessage(sender, this);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {
        listener.receiveNotaryMessageConfirmation(receiver, this);
    }
}
