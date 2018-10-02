package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import DomainObjects.Message;
import Domainlogic.BootstrapManager;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.Exceptions.SendFailedException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.IMessageListener;

import java.io.IOException;

public class Peer1 implements IMessageListener {

    public static void main(String[] args) throws NetworkJoinException, SendFailedException, PeerNotInitializedException, NotInContactListException, PeerCreateException, DataSaveException {
//        Contact ownContact = KnownContacts.contacts[0];
//
//        PeerDHT peer = PeerCreator.CreatePeer(ownContact.getName(), ownContact.getState().getPort());
//        Bootstrap.bootstrap(peer);
//
//        IMessageListener messageListener = new Peer1();
//        PeerCommunicator communicator = new PeerCommunicator(messageListener, peer, ownContact);
//        BootstrapManager bm = new BootstrapManager();
//        ContactManager cm = new ContactManager();
//        Contact ownContact = KnownContacts.contacts[0];
//        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
//        IMessageListener messageListener = new Peer1();
//        MessageManager msgManager = new MessageManager(messageListener, cm);
//        msgManager.setYou(KnownContacts.getMe());
//        msgManager.setMe(KnownContacts.getYou());
//
//        msgManager.sendMessage("KnownContacts.contacts[1]", "Hi Peer2");
        Contact ownContact = KnownContacts.contacts[0];
        ContactManager cm = new ContactManager(ownContact, KnownContacts.getContactList());
        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
        boolean success = PeerManager.bootstrap(new BootstrapInformation("127.0.0.1", 4000));

        if (!success) {
            System.out.println("Could not connect to DHT");
            return;
        }

        IMessageListener messageListener = new Peer1();
        MessageManager msgManager = new MessageManager(messageListener, cm);
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}