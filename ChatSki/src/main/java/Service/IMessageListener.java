package Service;

import DomainObjects.Contact;

public interface IMessageListener {
    void receiveMessage(Contact sender, String message);
}