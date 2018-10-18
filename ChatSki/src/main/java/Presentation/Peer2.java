package Presentation;

import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.Exceptions.SendFailedException;
import Service.Exceptions.PeerNotInitializedException;

public class Peer2 {

    public static void main(String[] args) throws NetworkJoinException, PeerCreateException, SendFailedException, PeerNotInitializedException {
        Peer p = new Peer(KnownContacts.contacts[1]);
        p.sendContactRequest(KnownContacts.contacts[0].getName());
    }
}