package DomainObjects;

import DomainObjects.Interfaces.IMessage;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;

public class GroupMessage implements Serializable, ITransmittable, IMessage {

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
    public String toString() {
        return sender.getName() + ": " + message;
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
