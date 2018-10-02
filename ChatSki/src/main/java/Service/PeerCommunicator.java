package Service;

import DomainObjects.*;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;
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
        PeerHolder.getOwnPeer().peer().objectDataReply(this::receiveTransmittable);
    }

    private static PeerAddress getPeerAddress(Contact contact) throws UnknownHostException {
        State contactState = contact.getState();
        InetAddress ipAddress = Inet4Address.getByName(contactState.getIp());
        return new PeerAddress(Number160.createHash(contact.getName()), ipAddress, contactState.getPort(), contactState.getPort());
    }

    private TransmissionConfirmation receiveTransmittable(PeerAddress sender, Object request) {
        ITransmittable transmittable = (ITransmittable) request;
        transmittable.handleReception(messageListener);
        return new TransmissionConfirmation();
    }

    public boolean sendDirect(Contact receiver, ITransmittable transmittable) throws UnknownHostException, PeerNotInitializedException {
        if (PeerHolder.getOwnPeer() == null) {
            throw new PeerNotInitializedException();
        }

        PeerAddress address = getPeerAddress(receiver);
        FutureDirect future = PeerHolder.getOwnPeer().peer().sendDirect(address).object(transmittable).start();
        future.awaitUninterruptibly();
        return future.isSuccess();
    }
}
