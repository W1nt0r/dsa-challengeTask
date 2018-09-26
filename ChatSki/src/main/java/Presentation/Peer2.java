package Presentation;

import DomainObjects.Contact;
import Service.IMessageListener;
import Service.PeerCommunicator;
import Service.PeerCreator;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;

public class Peer2 implements IMessageListener {

    public static void main(String[] args) throws IOException {
        Contact ownContact = KnownContacts.contacts[1];

        PeerDHT peer = PeerCreator.CreatePeer(ownContact.getName(), ownContact.getState().getPort());
        Bootstrap.bootstrap(peer);

        IMessageListener messageListener = new Peer2();
        PeerCommunicator communicator = new PeerCommunicator(messageListener, peer, ownContact);

        communicator.sendMessage(KnownContacts.contacts[0], "Hi");
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}