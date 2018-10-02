package DomainObjects;

import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class ContactResponse implements Serializable, ITransmittable {

    private Contact sender;

    private boolean accepted;

    public ContactResponse(Contact sender, boolean accepted) {
        this.sender = sender;
        this.accepted = accepted;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveContactResponse(sender, accepted);
    }
}
