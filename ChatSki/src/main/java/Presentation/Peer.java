package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.IMessageTransmitter;
import DomainObjects.Message;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.NotInContactListException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.Exceptions.SendFailedException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Service.Exceptions.PeerNotInitializedException;

import java.util.Scanner;

public class Peer implements IMessageTransmitter {

    private MessageManager messageManager;

    public Peer(Contact ownContact) throws NetworkJoinException, PeerCreateException {
        ContactManager contactManager = new ContactManager(ownContact, KnownContacts.getContactList());
        PeerManager.initializePeer(ownContact.getName(), ownContact.getState().getPort());
        boolean success = PeerManager.bootstrap(new BootstrapInformation("127.0.0.1", 4000));

        if (!success) {
            System.out.println("Could not connect to DHT");
            return;
        }

        messageManager = new MessageManager(this, contactManager);
    }

    public void sendMessage(String receiver, String message) throws SendFailedException, PeerNotInitializedException, NotInContactListException {
        boolean sent = messageManager.sendMessage(receiver, message);
        System.out.println("Message " + (sent ? "sent" : "not sent"));
    }

    public void sendContactRequest(Contact contact) throws SendFailedException, PeerNotInitializedException {
        boolean sent = messageManager.sendContactRequest(contact);
        System.out.println("Request " + (sent ? "sent" : "not sent"));
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        System.out.println(sender.getName() + " sent: " + message);
    }

    @Override
    public void receiveContactRequest(Contact sender) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(sender.getName() + " sent you a Contact-Request.");
        System.out.println("Would you like to accept? [Y/N]");
        String answer = scanner.nextLine();
        try {
            boolean sent = messageManager.sendContactResponse(sender, answer.toLowerCase().equals("y"));
            System.out.println("Response " + (sent ? "sent" : "not sent"));
        } catch (PeerNotInitializedException | SendFailedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveContactResponse(Contact sender, boolean accepted) {
        if (accepted) {
            System.out.println(sender.getName() + " accepted your Request");
        } else {
            System.out.println(sender.getName() + " denied your Request");
        }
    }

    @Override
    public void showException(Exception e) {
        System.err.println(e.getMessage());
    }
}
