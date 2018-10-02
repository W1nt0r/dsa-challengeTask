package Presentation;

import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;

public class Peer1 {

    public static void main(String[] args) throws NetworkJoinException, PeerCreateException {
        new Peer(KnownContacts.contacts[0]);
    }
}