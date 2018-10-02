package Presentation;

import DomainObjects.Contact;
import DomainObjects.State;

import java.util.HashMap;

public final class KnownContacts {
    public final static Contact[] contacts = new Contact[] {
            new Contact("Peer1", new State("127.0.0.1", 4001, true)),
            new Contact("Peer2", new State("127.0.0.1", 4002, true))
    };

    public static HashMap<String, Contact> getContactList() {
        HashMap<String, Contact> list = new HashMap<>();
        for (Contact c : contacts) {
            list.put(c.getName(), c);
        }
        return list;
    }

    public static Contact getMe() {
        return contacts[1];
    }

    public static Contact getYou() {
        return contacts[0];
    }
}
