package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Message;

public interface IMessageTransmitter {
    void receiveMessage(Contact sender, String message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveMessageConfirmation(Contact receiver, Message message);

    void receiveContactRequestConfirmation(Contact receiver);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void showThrowable(Throwable t);
}
