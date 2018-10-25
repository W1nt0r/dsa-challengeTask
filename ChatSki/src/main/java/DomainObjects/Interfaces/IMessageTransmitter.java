package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.GroupMessage;
import DomainObjects.Message;

public interface IMessageTransmitter {
    void receiveMessage(Contact sender, Message message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveGroupMessage(GroupMessage message);

    void receiveMessageConfirmation(Contact receiver, Message message);

    void receiveContactRequestConfirmation(Contact receiver);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void showThrowable(Throwable t);
}
