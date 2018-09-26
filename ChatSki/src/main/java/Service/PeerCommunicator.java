package Service;

import DomainObjects.Contact;
import DomainObjects.Message;
import DomainObjects.MessageConfirmation;
import DomainObjects.State;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PeerCommunicator {

    private final IMessageListener messageListener;
    private final PeerDHT ownPeer;
    private final Contact ownContact;

    public PeerCommunicator(IMessageListener messageListener, PeerDHT ownPeer, Contact ownContact) {
        this.messageListener = messageListener;
        this.ownPeer = ownPeer;
        this.ownContact = ownContact;
        ownPeer.peer().objectDataReply(this::receiveMessage);
    }

    private static PeerAddress getPeerAddress(Contact contact) throws UnknownHostException {
        State contactState = contact.getState();
        InetAddress ipAddress = Inet4Address.getByName(contactState.getIp());
        return new PeerAddress(Number160.createHash(contact.getName()), ipAddress, contactState.getPort(), contactState.getPort());
    }

    private MessageConfirmation receiveMessage(PeerAddress sender, Object request) {
        Message msg = (Message) request;
        messageListener.receiveMessage(msg.getSender(), msg.getMessage());
        return new MessageConfirmation();
    }

    public boolean sendMessage(Contact receiver, String message) throws UnknownHostException {
        PeerAddress address = getPeerAddress(receiver);
        Message msg = new Message(ownContact, message);
        FutureDirect future = ownPeer.peer().sendDirect(address).object(msg).start();
        future.awaitUninterruptibly();
        return future.isSuccess();
    }
}
