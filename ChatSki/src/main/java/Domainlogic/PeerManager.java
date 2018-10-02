package Domainlogic;

import DomainObjects.BootstrapInformation;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Service.PeerHolder;

import java.io.IOException;
import java.net.UnknownHostException;

public class PeerManager {
    public static void initializePeer(String id, int port) throws PeerCreateException {
        try {
            PeerHolder.initializePeer(id, port);
        } catch (IOException ex) {
            throw new PeerCreateException(ex);
        }
    }

    public static boolean bootstrap(BootstrapInformation bootstrapInfo) throws NetworkJoinException {
        try {
            return PeerHolder.bootstrap(bootstrapInfo);
        } catch (UnknownHostException e) {
            throw new NetworkJoinException(e);
        }
    }
}