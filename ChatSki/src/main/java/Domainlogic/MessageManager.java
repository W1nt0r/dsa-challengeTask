package Domainlogic;

import DomainObjects.Contact;
import DomainObjects.ContactRequest;
import DomainObjects.ContactResponse;
import DomainObjects.Interfaces.ITransmittable;
import DomainObjects.Message;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.PeerNotInitializedException;
import DomainObjects.Interfaces.IMessageListener;
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

        return send(receiver, msg);
    }

    public boolean sendContactResponse(Contact receiver, boolean accepted) throws PeerNotInitializedException, SendFailedException {
        ContactResponse response = new ContactResponse(contactManager.getOwnContact(), accepted);
        return send(receiver, response);
    }

    public boolean sendContactRequest(Contact receiver) throws PeerNotInitializedException, SendFailedException {
        ContactRequest request = new ContactRequest(contactManager.getOwnContact());
        return send(receiver, request);
    }

    private boolean send(Contact receiver, ITransmittable transmittable) throws SendFailedException, PeerNotInitializedException {
        try {
            return communicator.sendDirect(receiver, transmittable);
        } catch (UnknownHostException e) {
            throw new SendFailedException();
        }
    }
}
