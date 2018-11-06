package Domainlogic;

import DomainObjects.*;
import DomainObjects.Interfaces.*;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.PeerCommunicator;
import Service.StateService;

import java.net.UnknownHostException;
import java.util.*;

public class MessageManager implements IMessageListener, IMessageSender {

    private static final String GROUP_CREATION_MESSAGE = "%s created group %s";

    private final IMessageTransmitter messageTransmitter;
    private final IStateListener stateListener;
    private final PeerCommunicator communicator;
    private final ContactManager contactManager;
    private final Map<ICollocutor, ChatSequence> activeChats;

    public MessageManager(IMessageTransmitter messageListener,
                          IStateListener stateListener,
                          ContactManager contactManager) throws PeerNotInitializedException {
        this.messageTransmitter = messageListener;
        this.stateListener = stateListener;
        this.contactManager = contactManager;
        communicator = new PeerCommunicator(this);
        activeChats = new HashMap<>();
    }

    public synchronized List<IMessage> getChatHistory(ICollocutor collocutor) {
        ChatSequence conversation = activeChats.get(collocutor);

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

    public synchronized void sendGroupCreation(String groupName,
                                               Set<Contact> members) {
        Contact ownContact = contactManager.getOwnContact();
        String firstMessage = String.format(GROUP_CREATION_MESSAGE,
                ownContact.getName(), groupName);
        members.add(ownContact);
        Group newGroup = new Group(groupName, members);
        sendGroupMessage(newGroup, firstMessage);
        try {
            contactManager.addGroup(newGroup);
        } catch (DataSaveException e) {
            messageTransmitter.showThrowable(e);
        }
    }

    @Override
    public synchronized void sendMessage(Contact receiver, String message) {
        Message msg = new Message(contactManager.getOwnContact(), message);
        try {
            send(receiver, msg);
        } catch (PeerNotInitializedException e) {
            messageTransmitter.showThrowable(e);
        }
    }

    @Override
    public synchronized void sendGroupMessage(Group group, String message) {
        Contact ownContact = contactManager.getOwnContact();
        GroupMessage groupMessage = new GroupMessage(group, ownContact,
                message);
        appendMessageToChatSequence(group, groupMessage);
        for (Contact receiver : group.getMembers()) {
            if (!receiver.equals(ownContact)) {
                try {
                    send(receiver, groupMessage);
                } catch (PeerNotInitializedException e) {
                    messageTransmitter.showThrowable(e);
                }
            }
        }
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

    private synchronized boolean isChatActive(ICollocutor collocutor) {
        return activeChats.containsKey(collocutor);
    }

    private synchronized void appendMessageToChatSequence(ICollocutor collocutor,
                                                          IMessage message) {
        if (!isChatActive(collocutor)) {
            activeChats.put(collocutor, new ChatSequence());
        }

        activeChats.get(collocutor).appendMessage(message);
        messageTransmitter.messagesUpdated(collocutor);
    }

    @Override
    public synchronized void receiveMessage(Contact sender, Message message) {
        appendMessageToChatSequence(sender, message);
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
    public void receiveGroupMessage(GroupMessage message) {
        if (!contactManager.isKnownGroup(message.getGroup())) {
            try {
                contactManager.addGroup(message.getGroup());
            } catch (DataSaveException e) {
                messageTransmitter.showThrowable(e);
            }
        }
        appendMessageToChatSequence(message.getGroup(), message);
    }

    @Override
    public synchronized void receiveMessageConfirmation(Contact receiver,
                                                        Message message) {
        appendMessageToChatSequence(receiver, message);
    }

    @Override
    public synchronized void receiveContactRequestConfirmation(
            Contact receiver) {

    }

    @Override
    public synchronized void receiveContactResponseConfirmation(
            Contact receiver, boolean accepted) {
        if (accepted) {
            try {
                contactManager.addContact(receiver.getName());
                messageTransmitter.receiveContactResponseConfirmation(receiver, true);
            } catch (DataSaveException e) {
                messageTransmitter.showThrowable(e);
            }
        }
    }

    @Override
    public void receiveGroupMessageConfirmation(Contact receiver,
                                                GroupMessage message) {

    }

    @Override
    public synchronized void receiveThrowable(Throwable t) {
        messageTransmitter.showThrowable(t);
    }
}
