package DomainObjects;

import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class GroupMessage implements Serializable, ITransmittable {

    private Group group;
    private Contact sender;
    private String message;

    public GroupMessage(Group group, Contact sender, String message) {
        this.group = group;
        this.sender = sender;
        this.message = message;
    }

    public Group getGroup() {
        return group;
    }

    public String getMessage() {
        return message;
    }

    public Contact getSender() {
        return sender;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveGroupMessage(this);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {
        listener.receiveGroupMessageConfirmation(receiver, this);
    }
}
