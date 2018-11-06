package DomainObjects;

import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class StateInformation implements Serializable, ITransmittable {

    private Contact contact;

    public StateInformation(Contact contact) {
        this.contact = contact;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveStateInformation(contact);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {
    }
}
