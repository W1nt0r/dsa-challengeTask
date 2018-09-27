package Presentation;

import DomainObjects.Contact;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.SendFailedException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Service.Exceptions.PeerNotInitializedException;
import Service.IMessageListener;

import java.io.IOException;

public class Peer2 implements IMessageListener {

    public static void main(String[] args) throws IOException, NetworkJoinException, SendFailedException, PeerNotInitializedException, NotInContactListException {
//        Contact ownContact = KnownContacts.contacts[1];
//
//        PeerDHT peer = PeerCreator.CreatePeer(ownContact.getName(), ownContact.getState().getPort());
//        Bootstrap.bootstrap(peer);
//
//        IMessageListener messageListener = new Peer2();
//        PeerCommunicator communicator = new PeerCommunicator(messageListener, peer, ownContact);
//
//        communicator.sendMessage(KnownContacts.contacts[0], "Hi");
        Contact ownContact = KnownContacts.contacts[1];
        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
        IMessageListener messageListener = new Peer2();
        MessageManager msgManager = new MessageManager(messageListener);
        //msgManager.sendMessage("KnownContacts.contacts[0]", "Hi Peer1");
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}