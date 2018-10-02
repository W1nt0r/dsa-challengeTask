package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import Domainlogic.BootstrapManager;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.PeerManager;
import Service.Exceptions.DataSaveException;

import java.util.Scanner;

public class Example {
    public static void main(String args[]) throws DataSaveException, PeerCreateException, NetworkJoinException {
        Scanner scanner = new Scanner(System.in);
        ContactManager cm = new ContactManager();
        BootstrapManager bm = new BootstrapManager();

        if (cm.isOwnContactEmpty()) {
            System.out.println("Please enter your name:");
            String name = scanner.nextLine();
            cm.setOwnContactName(name);
        }

        if (bm.isBootstrapInfoEmpty()) {
            bm.setBootstrapInfo(getNewBootstrapInformation());
        }

        Contact ownContact = cm.getOwnContact();
        PeerManager.initializePeer(ownContact.getName(), 4001);

        boolean connected = PeerManager.bootstrap(bm.getBootstrapInfo());

        while (!connected) {
            System.out.println("Could not connect to Bootstrap-Peer");
            bm.setBootstrapInfo(getNewBootstrapInformation());
            connected = PeerManager.bootstrap(bm.getBootstrapInfo());
        }

        BootstrapInformation info = bm.getBootstrapInfo();
        System.out.println("Hello " + ownContact.getName());
        System.out.println("Bootstrap-IP: " + info.getIpAddress() + " Port: " + info.getPort());

        //System.out.println(peer.get(Number160.createHash("TestFile")).start().awaitUninterruptibly().data().object().toString());
    }

    private static BootstrapInformation getNewBootstrapInformation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter Bootstrap-IP:");
        String ip = scanner.nextLine();
        System.out.println("Please enter Bootstrap-Port:");
        int port = scanner.nextInt();
        return new BootstrapInformation(ip, port);
    }
}
