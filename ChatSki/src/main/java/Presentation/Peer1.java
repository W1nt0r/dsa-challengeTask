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

public class Peer1 implements IMessageListener {

    public static void main(String[] args) throws IOException, NetworkJoinException, SendFailedException, PeerNotInitializedException, NotInContactListException {
//        Contact ownContact = KnownContacts.contacts[0];
//
//        PeerDHT peer = PeerCreator.CreatePeer(ownContact.getName(), ownContact.getState().getPort());
//        Bootstrap.bootstrap(peer);
//
//        IMessageListener messageListener = new Peer1();
//        PeerCommunicator communicator = new PeerCommunicator(messageListener, peer, ownContact);
        Contact ownContact = KnownContacts.contacts[0];
        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
        IMessageListener messageListener = new Peer1();
        MessageManager msgManager = new MessageManager(messageListener);
        msgManager.setYou(KnownContacts.getMe());
        msgManager.setMe(KnownContacts.getYou());

        msgManager.sendMessage("KnownContacts.contacts[1]", "Hi Peer2");
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}