package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Group;
import DomainObjects.GroupMessage;
import DomainObjects.Message;

public interface IMessageListener {
    void receiveMessage(Contact sender, Message message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveGroupMessage(GroupMessage message);

    void receiveMessageConfirmation(Contact receiver, Message message);

    void receiveContactRequestConfirmation(Contact receiver);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void receiveGroupMessageConfirmation(Contact receiver,
                                         GroupMessage message);

    void receiveThrowable(Throwable t);

    void receiveStateInformation(Contact contact);
}