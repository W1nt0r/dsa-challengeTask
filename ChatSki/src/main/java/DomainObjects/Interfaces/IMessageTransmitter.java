package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.NotaryMessage;

public interface IMessageTransmitter {

    void receiveNotaryMessage(Contact sender, NotaryMessage message);

    void receiveNotaryMessageResponse(Contact sender, NotaryMessage message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void messagesUpdated(ICollocutor collocutor);

    void showThrowable(Throwable t);
}
