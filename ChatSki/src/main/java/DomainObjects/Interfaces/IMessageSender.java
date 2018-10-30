package DomainObjects.Interfaces;

import DomainObjects.Contact;
import DomainObjects.Group;

public interface IMessageSender {
    void sendMessage(Contact receiver, String message);
    void sendGroupMessage(Group receiver, String message);
}
