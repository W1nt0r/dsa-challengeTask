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
    private Contact me = KnownContacts.getMe();
    private Contact you = KnownContacts.getYou();

    public MessageManager(IMessageListener messageListener) throws NetworkJoinException {
        this.messageListener = messageListener;
        communicator = new PeerCommunicator(messageListener);
    }

    public void sendMessage(String receiverName, String message) throws NotInContactListException, SendFailedException, PeerNotInitializedException {
        if (!isContact(receiverName)) {
            throw new NotInContactListException();
        }
        Contact receiver = getContact(receiverName);
        Message msg = new Message(me, message);

        try {
            communicator.sendMessage(receiver, msg);
        } catch (UnknownHostException e) {
            throw new SendFailedException();
        }
    }

    private Contact getContact(String receiverName) {
        return you;
    }

    private boolean isContact(String receiverName) {
        return true;
    }

    //nur für Prototyp-Phase
    public void setMe(Contact me) {
        this.me = me;
    }

    public void setYou(Contact you) {
        this.you = you;
    }
}
