package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.GroupMessage;
import DomainObjects.Message;
import DomainObjects.NotaryMessage;

public interface IMessageListener {
    void receiveMessage(Contact sender, Message message);

    void receiveNotaryMessage(Contact sender, NotaryMessage message);

    void receiveNotaryMessageResponse(Contact sender, NotaryMessage message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveGroupMessage(GroupMessage message);

    void receiveMessageConfirmation(Contact receiver, Message message);

    void receiveNotaryMessageConfirmation(Contact receiver, NotaryMessage message);

    void receiveContactRequestConfirmation(Contact receiver);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void receiveGroupMessageConfirmation(Contact receiver,
                                         GroupMessage message);

    void receiveThrowable(Throwable t);

    void receiveStateInformation(Contact contact);
}