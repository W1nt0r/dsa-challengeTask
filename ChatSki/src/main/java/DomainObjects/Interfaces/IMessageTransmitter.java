package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Message;

public interface IMessageTransmitter {
    void receiveMessage(Contact sender, Message message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void showException(Exception e);
}
