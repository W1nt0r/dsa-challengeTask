package Service;

import DomainObjects.Contact;
import DomainObjects.Message;
import DomainObjects.MessageConfirmation;
import DomainObjects.State;
import Service.Exceptions.PeerNotInitializedException;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PeerCommunicator {

    private final IMessageListener messageListener;

    public PeerCommunicator(IMessageListener messageListener) {
        this.messageListener = messageListener;
        PeerHolder.getOwnPeer().peer().objectDataReply(this::receiveMessage);
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

    public boolean sendMessage(Contact receiver, Message message) throws UnknownHostException, PeerNotInitializedException {
        if (PeerHolder.getOwnPeer() == null) {
            throw new PeerNotInitializedException();
        }

        PeerAddress address = getPeerAddress(receiver);
        FutureDirect future = PeerHolder.getOwnPeer().peer().sendDirect(address).object(message).start();
        future.awaitUninterruptibly();
        return future.isSuccess();
    }
}
