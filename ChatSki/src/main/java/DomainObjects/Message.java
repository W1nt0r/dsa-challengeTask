package DomainObjects;

import DomainObjects.Interfaces.ITransmittable;
import DomainObjects.Interfaces.IMessageListener;

import java.io.Serializable;

public class Message implements Serializable, ITransmittable {
    private final Contact sender;
    private final String message;

    public Message(Contact sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveMessage(sender, this);
    }

    public String getMessage() {
        return message;
    }
}
