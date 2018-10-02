package Domainlogic;

import DomainObjects.Contact;
import DomainObjects.Message;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.SendFailedException;
import Presentation.KnownContacts;
import Service.Exceptions.PeerNotInitializedException;
import Service.IMessageListener;
import Service.PeerCommunicator;

import java.net.UnknownHostException;

public class MessageManager {

    private final IMessageListener messageListener;
    private final PeerCommunicator communicator;
    private final ContactManager contactManager;

    public MessageManager(IMessageListener messageListener, ContactManager contactManager) {
        this.messageListener = messageListener;
        this.contactManager = contactManager;
        communicator = new PeerCommunicator(messageListener);
    }

    public boolean sendMessage(String receiverName, String message) throws NotInContactListException, SendFailedException, PeerNotInitializedException {
        if (!contactManager.isContact(receiverName)) {
            throw new NotInContactListException();
        }
        Contact receiver = contactManager.getContact(receiverName);
        Message msg = new Message(contactManager.getOwnContact(), message);

        try {
            return communicator.sendMessage(receiver, msg);
        } catch (UnknownHostException e) {
            throw new SendFailedException();
        }
    }
}
