package Presentation;

import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Service.Exceptions.PeerNotInitializedException;

public class Peer1 {

    public static void main(String[] args) throws NetworkJoinException, PeerCreateException, PeerNotInitializedException {
        new Peer(KnownContacts.contacts[0]);
    }
}