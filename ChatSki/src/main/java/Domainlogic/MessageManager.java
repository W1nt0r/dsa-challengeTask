package Domainlogic;

import DomainObjects.*;
import DomainObjects.Interfaces.IMessageTransmitter;
import DomainObjects.Interfaces.IStateListener;
import DomainObjects.Interfaces.ITransmittable;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import DomainObjects.Interfaces.IMessageListener;
import Service.PeerCommunicator;
import Service.StateService;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager implements IMessageListener {

    private final IMessageTransmitter messageTransmitter;
    private final IStateListener stateListener;
    private final PeerCommunicator communicator;
    private final ContactManager contactManager;
    private final Map<String, ChatSequence> activeChats;

    public MessageManager(IMessageTransmitter messageListener,
                          IStateListener stateListener,
                          ContactManager contactManager) throws PeerNotInitializedException {
        this.messageTransmitter = messageListener;
        this.stateListener = stateListener;
        this.contactManager = contactManager;
        communicator = new PeerCommunicator(this);
        activeChats = new HashMap<>();
    }

    public synchronized void sendMessage(String receiverName,
                                         String message) throws NotInContactListException, PeerNotInitializedException {
        if (!contactManager.isContact(receiverName)) {
            throw new NotInContactListException();
        }
        Contact receiver = contactManager.getContact(receiverName);
        Message msg = new Message(contactManager.getOwnContact(), message);

        send(receiver, msg);
    }

    public synchronized List<Message> getChatHistory(String username) {
        ChatSequence conversation = activeChats.get(username);

        return conversation == null ? new ArrayList<>() : conversation.getChatMessages();
    }

    public synchronized void sendContactResponse(Contact receiver,
                                                 boolean accepted) throws PeerNotInitializedException {
        ContactResponse response = new ContactResponse(contactManager.getOwnContact(), accepted);
        send(receiver, response);
    }

    public synchronized void sendContactRequest(
            String receiver) throws PeerNotInitializedException {
        ContactRequest request = new ContactRequest(contactManager.getOwnContact());
        Contact receiverContact = contactManager.createContactFromName(receiver);

        send(receiverContact, request);
    }

    private synchronized void send(Contact receiver,
                      ITransmittable transmittable) throws PeerNotInitializedException {
        StateService.LoadStateFromDht(receiver.getName(),
                state -> send(receiver, state, transmittable), stateListener::showThrowable);
    }

    private void send(Contact receiver, State currentState,
                      ITransmittable transmittable) {
        updateState(receiver, currentState);
        if (currentState == null || !currentState.isOnline()) {
            messageTransmitter.showThrowable(new SendFailedException("Peer is not online"));
            return;
        }
        try {
            communicator.sendDirect(receiver, transmittable);
        } catch (UnknownHostException e) {
            messageTransmitter.showThrowable(new SendFailedException("Unknown" +
                    " IP"));
        } catch (PeerNotInitializedException e) {
            messageTransmitter.showThrowable(e);
        }
    }

    private void updateState(Contact contact, State newState) {
        contact.setState(newState);
        stateListener.updateContactState(contact);
    }

    private synchronized boolean isChatActive(String username) {
        return activeChats.containsKey(username);
    }

    private synchronized void appendMessageToChatSequence(String username,
                                                          Message message) {
        if (!isChatActive(username)) {
            activeChats.put(username, new ChatSequence());
        }

        activeChats.get(username).appendMessage(message);
    }

    @Override
    public synchronized void receiveMessage(Contact sender, Message message) {
        appendMessageToChatSequence(sender.getName(), message);

        messageTransmitter.receiveMessage(sender, message);
    }

    @Override
    public synchronized void receiveContactRequest(Contact sender) {
        messageTransmitter.receiveContactRequest(sender);
    }

    @Override
    public synchronized void receiveContactResponse(Contact sender,
                                                    boolean accepted) {
        messageTransmitter.receiveContactResponse(sender, accepted);
        if (accepted) {
            try {
                contactManager.addContact(sender.getName());
            } catch (DataSaveException e) {
                messageTransmitter.showThrowable(e);
            }
        }
    }

    @Override
    public synchronized void receiveMessageConfirmation(Contact receiver,
                                                        Message message) {
        appendMessageToChatSequence(receiver.getName(), message);
        messageTransmitter.receiveMessageConfirmation(receiver, message);
    }

    @Override
    public synchronized void receiveContactRequestConfirmation(
            Contact receiver) {
        System.out.println("Received contact request confirmation");
    }

    @Override
    public synchronized void receiveContactResponseConfirmation(
            Contact receiver, boolean accepted) {
        if (accepted) {
            try {
                contactManager.addContact(receiver.getName());
                messageTransmitter.receiveContactResponseConfirmation(receiver, accepted);
            } catch (DataSaveException e) {
                messageTransmitter.showThrowable(e);
            }
        }
    }

    @Override
    public synchronized void receiveThrowable(Throwable t) {
        messageTransmitter.showThrowable(t);
    }
}
