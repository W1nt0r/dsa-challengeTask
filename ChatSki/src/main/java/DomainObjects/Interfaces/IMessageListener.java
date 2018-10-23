package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Message;

public interface IMessageListener {
    void receiveMessage(Contact sender, Message message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveMessageConfirmation(Contact receiver, Message message);

    void receiveContactRequestConfirmation(Contact receiver);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void receiveThrowable(Throwable t);
}