package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.GroupMessage;
import DomainObjects.Message;

public interface IMessageTransmitter {

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void receiveContactResponseConfirmation(Contact receiver, boolean accepted);

    void messagesUpdated(ICollocutor collocutor);

    void showThrowable(Throwable t);
}
