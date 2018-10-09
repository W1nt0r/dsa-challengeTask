package DomainObjects.Interfaces;

import DomainObjects.Contact;

public interface IMessageTransmitter {
    void receiveMessage(Contact sender, String message);

    void receiveContactRequest(Contact sender);

    void receiveContactResponse(Contact sender, boolean accepted);

    void showException(Exception e);
}
