package Service;

import DomainObjects.*;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.PeerNotInitializedException;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PeerCommunicator {

    private final IMessageListener messageListener;

    public PeerCommunicator(
            IMessageListener messageListener) throws PeerNotInitializedException {
        this.messageListener = messageListener;
        PeerHolder.getOwnPeer().peer().objectDataReply(this::receiveTransmittable);
    }

    private static PeerAddress getPeerAddress(
            Contact contact) throws UnknownHostException {
        State contactState = contact.getState();
        InetAddress ipAddress = Inet4Address.getByName(contactState.getIp());
        return new PeerAddress(Number160.createHash(contact.getName()), ipAddress, contactState.getPort(), contactState.getPort());
    }

    private TransmissionConfirmation receiveTransmittable(PeerAddress sender,
                                                          Object request) {
        ITransmittable transmittable = (ITransmittable) request;
        new Thread(() -> transmittable.handleReception(messageListener)).start();
        return new TransmissionConfirmation();
    }

    public void sendDirect(Contact receiver, ITransmittable transmittable)
            throws UnknownHostException, PeerNotInitializedException {
        PeerAddress address = getPeerAddress(receiver);
        FutureDirect future = PeerHolder.getOwnPeer().peer().sendDirect(address).object(transmittable).start();
        future.addListener(new BaseFutureListener<FutureDirect>() {
            @Override
            public void operationComplete(FutureDirect future) throws SendFailedException {
                if (future.isSuccess()) {
                    transmittable.handleConfirmation(receiver, messageListener);
                } else {
                    throw new SendFailedException("Peer is not online");
                }
            }

            @Override
            public void exceptionCaught(Throwable t) {
                messageListener.receiveThrowable(t);
            }
        });
    }
}
