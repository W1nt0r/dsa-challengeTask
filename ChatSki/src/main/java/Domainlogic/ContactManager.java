package Domainlogic;

import DomainObjects.Contact;
import DomainObjects.State;
import Service.DataSaver;
import Service.Exceptions.DataSaveException;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class ContactManager {

    private final String OWN_CONTACT_FILE = "OwnContact.ser";
    private final String CONTACT_LIST_FILE = "ContactList.ser";

    private Contact ownContact;
    private HashMap<String, Contact> contactList;

    public ContactManager() throws DataSaveException {
        loadOwnContact();
        loadContactList();
    }

    public ContactManager(Contact ownContact, HashMap<String, Contact> contactList) {
        this.ownContact = ownContact;
        this.contactList = contactList;
    }

    public Contact getOwnContact() {
        return ownContact;
    }

    public void save() throws DataSaveException {
        saveOwnContact();
        saveContactList();
    }

    public boolean isOwnContactEmpty() {
        return ownContact.getName() == null;
    }

    public void setOwnContactName(String name) throws DataSaveException {
        ownContact.setName(name);
        saveOwnContact();
    }

    public boolean isContact(String contactName) {
        return contactList.containsKey(contactName);
    }

    public Contact getContact(String contactName) {
        return contactList.get(contactName);
    }

    public void addContact(String contactName) throws DataSaveException {
        Contact newContact = new Contact(contactName, State.EMPTY_STATE);
        contactList.put(contactName, newContact);
        saveContactList();
    }

    private void loadOwnContact() throws DataSaveException {
        DataSaver<Contact> saver = new DataSaver<>(OWN_CONTACT_FILE);
        try {
            ownContact = saver.loadData();
        } catch (FileNotFoundException e) {
            ownContact = new Contact(null, State.EMPTY_STATE);
        }
    }

    private void saveOwnContact() throws DataSaveException {
        DataSaver<Contact> saver = new DataSaver<>(OWN_CONTACT_FILE);
        saver.saveData(ownContact);
    }

    private void loadContactList() throws DataSaveException {
        DataSaver<HashMap<String, Contact>> saver = new DataSaver<>(CONTACT_LIST_FILE);
        try {
            contactList = saver.loadData();
        } catch (FileNotFoundException e) {
            contactList = new HashMap<>();
        }
    }

    private void saveContactList() throws DataSaveException {
        DataSaver<HashMap<String, Contact>> saver = new DataSaver<>(CONTACT_LIST_FILE);
        saver.saveData(contactList);
    }
}
