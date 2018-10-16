package Domainlogic;

import DomainObjects.*;
import DomainObjects.Interfaces.IMessageTransmitter;
import DomainObjects.Interfaces.ITransmittable;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotAvailableException;
import Service.Exceptions.PeerNotInitializedException;
import DomainObjects.Interfaces.IMessageListener;
import Service.PeerCommunicator;
import Service.StateService;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager implements IMessageListener {

    private final IMessageTransmitter messageTransmitter;
    private final PeerCommunicator communicator;
    private final ContactManager contactManager;
    private final Map<String, ChatSequence> activeChats;

    public MessageManager(IMessageTransmitter messageListener, ContactManager contactManager) throws PeerNotInitializedException {
        this.messageTransmitter = messageListener;
        this.contactManager = contactManager;
        communicator = new PeerCommunicator(this);
        activeChats = new HashMap<>();
    }

    public boolean sendMessage(String receiverName, String message) throws NotInContactListException, SendFailedException, PeerNotInitializedException {
        if (!contactManager.isContact(receiverName)) {
            throw new NotInContactListException();
        }
        Contact receiver = contactManager.getContact(receiverName);
        Message msg = new Message(contactManager.getOwnContact(), message);
        appendMessageToChatSequence(receiverName, msg);

        return send(receiver, msg);
    }

    public synchronized List<Message> getChatHistory(String username) {
        return activeChats.get(username).getChatMessages();
    }

    public boolean sendContactResponse(Contact receiver, boolean accepted) throws PeerNotInitializedException, SendFailedException {
        ContactResponse response = new ContactResponse(contactManager.getOwnContact(), accepted);
        boolean sent = send(receiver, response);
        if (sent && accepted) {
            try {
                contactManager.addContact(receiver.getName());
            } catch (DataSaveException e) {
                messageTransmitter.showException(e);
            }
        }
        return sent;
    }

    public boolean sendContactRequest(String receiver) throws PeerNotInitializedException, SendFailedException {
        ContactRequest request = new ContactRequest(contactManager.getOwnContact());
        Contact receiverContact = contactManager.createContactFromName(receiver);

        return send(receiverContact, request);
    }

    private boolean send(Contact receiver, ITransmittable transmittable) throws SendFailedException, PeerNotInitializedException {
        try {
            State receiverState = StateService.LoadStateFromDht(receiver.getName());

            if (!receiverState.isOnline()) {
                throw new SendFailedException("Peer is not online");
            }

            receiver.setState(receiverState);
            return communicator.sendDirect(receiver, transmittable);
        } catch (UnknownHostException ex) {
            throw new SendFailedException();
        } catch (PeerNotAvailableException ex) {
            throw new SendFailedException("Peer is not online");
        }
    }

    private synchronized boolean isChatActive(String username) {
        return activeChats.containsKey(username);
    }

    private synchronized void appendMessageToChatSequence(String username, Message message) {
        if(!isChatActive(username)) {
            activeChats.put(username, new ChatSequence());
        }

        activeChats.get(username).appendMessage(message);
    }

    @Override
    public void receiveMessage(Contact sender, Message message) {
        appendMessageToChatSequence(sender.getName(), message);

        messageTransmitter.receiveMessage(sender, message.getMessage());
    }

    @Override
    public void receiveContactRequest(Contact sender) {
        messageTransmitter.receiveContactRequest(sender);
    }

    @Override
    public void receiveContactResponse(Contact sender, boolean accepted) {
        messageTransmitter.receiveContactResponse(sender, accepted);
        if (accepted) {
            try {
                contactManager.addContact(sender.getName());
            } catch (DataSaveException e) {
                messageTransmitter.showException(e);
            }
        }
    }
}
