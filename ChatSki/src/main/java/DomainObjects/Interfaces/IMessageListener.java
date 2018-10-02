package DomainObjects.Interfaces;

import DomainObjects.Contact;

public interface IMessageListener {
    void receiveMessage(Contact sender, String message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);
}