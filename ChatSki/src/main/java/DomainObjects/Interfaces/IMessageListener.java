package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Message;

public interface IMessageListener {
    void receiveMessage(Contact sender, Message message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);
}