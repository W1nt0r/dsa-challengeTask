package Presentation;

import DomainObjects.Contact;
import Service.IMessageListener;
import Service.PeerCommunicator;
import Service.PeerCreator;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;

public class Peer1 implements IMessageListener {

    public static void main(String[] args) throws IOException {
        Contact ownContact = KnownContacts.contacts[0];

        PeerDHT peer = PeerCreator.CreatePeer(ownContact.getName(), ownContact.getState().getPort());
        Bootstrap.bootstrap(peer);

        IMessageListener messageListener = new Peer1();
        PeerCommunicator communicator = new PeerCommunicator(messageListener, peer, ownContact);
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }
}