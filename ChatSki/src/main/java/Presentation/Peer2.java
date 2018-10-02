package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.Exceptions.SendFailedException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Service.Exceptions.PeerNotInitializedException;
import Service.IMessageListener;

import java.io.IOException;

public class Peer2 implements IMessageListener {

    public static void main(String[] args) throws NetworkJoinException, PeerCreateException, SendFailedException, PeerNotInitializedException, NotInContactListException {
        Contact ownContact = KnownContacts.contacts[1];
        ContactManager cm = new ContactManager(ownContact, KnownContacts.getContactList());
        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
        boolean success = PeerManager.bootstrap(new BootstrapInformation("127.0.0.1", 4000));

        if (!success) {
            System.out.println("Could not connect to DHT");
            return;
        }

        IMessageListener messageListener = new Peer1();
        MessageManager msgManager = new MessageManager(messageListener, cm);
        boolean sent = msgManager.sendMessage(KnownContacts.contacts[0].getName(), "Hello, this is peer 2");

        System.out.println("Message " + (sent ? "sent" : "not sent"));
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}