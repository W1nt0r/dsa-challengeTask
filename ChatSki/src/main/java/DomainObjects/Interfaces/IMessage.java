package DomainObjects.Interfaces;

import DomainObjects.Contact;

public interface IMessage {
    Contact getSender();

    String getMessage();
}
