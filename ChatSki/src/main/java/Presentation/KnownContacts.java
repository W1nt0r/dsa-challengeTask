package Presentation;

import DomainObjects.Contact;
import DomainObjects.State;

public final class KnownContacts {
    public final static Contact[] contacts = new Contact[] {
            new Contact("Peer1", new State("127.0.0.1", 4001, true)),
            new Contact("Peer2", new State("127.0.0.1", 4002, true))
    };
}
