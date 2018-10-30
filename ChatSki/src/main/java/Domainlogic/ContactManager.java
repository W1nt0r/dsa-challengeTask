package Domainlogic;

import DomainObjects.Contact;
import DomainObjects.Group;
import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IStateListener;
import DomainObjects.State;
import Service.DataSaver;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.Exceptions.ReplicationException;
import Service.StateService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactManager {

    private static final String OWN_CONTACT_FILE = "OwnContact.ser";
    private static final String CONTACT_LIST_FILE = "ContactList.ser";
    private static final String GROUP_LIST_FILE = "GroupList.ser";
    private final IStateListener stateListener;

    private Contact ownContact;

    private HashMap<String, Contact> contactList;
    private HashMap<String, Group> groupList;

    public List<ICollocutor> getCollocutors() {
        ArrayList<ICollocutor> collocutors = new ArrayList<>();
        collocutors.addAll(contactList.values());
        collocutors.addAll(groupList.values());
        return collocutors;
    }

    public List<Contact> getContactList() {
        return new ArrayList<>(contactList.values());
    }

    public ContactManager(
            IStateListener stateListener) throws DataSaveException {
        this.stateListener = stateListener;
        loadOwnContact();
        loadContactList();
        loadGroupList();
    }

    public Contact getOwnContact() {
        return ownContact;
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
        Contact newContact = createContactFromName(contactName);
        contactList.put(contactName, newContact);
        saveContactList();
        try {
            updateState(newContact);
        } catch (PeerNotInitializedException e) {
            stateListener.showThrowable(e);
        }
    }

    public void addGroup(Group newGroup) throws DataSaveException {
        groupList.put(newGroup.getName(), newGroup);
        saveGroupList();
    }

    public boolean isKnownGroup(Group group) {
        return groupList.containsKey(group.getName());
    }

    public Contact createContactFromName(String contactName) {
        return new Contact(contactName, State.EMPTY_STATE);
    }

    public void writeOwnStateToDHT(String stateId,
            boolean online) throws PeerNotInitializedException, ReplicationException {
        ownContact.getState().setOnline(online);

        StateService.SaveStateToDht(stateId, ownContact.getName(),
                ownContact.getState(), stateListener::replicationFinished);
    }

    public void updateStates() throws PeerNotInitializedException {
        for (Contact contact : contactList.values()) {
            updateState(contact);
        }
    }

    public void updateState(
            Contact contact) throws PeerNotInitializedException {
        StateService.LoadStateFromDht(contact.getName(), state -> {
            contact.setState(state);
            stateListener.updateContactState(contact);
        }, stateListener::showThrowable);
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
//        DataSaver<Contact> saver = new DataSaver<>(OWN_CONTACT_FILE);
//        saver.saveData(ownContact);
    }

    private void loadContactList() throws DataSaveException {
        DataSaver<HashMap<String, Contact>> saver = new DataSaver<>(CONTACT_LIST_FILE);
        try {
            contactList = saver.loadData();
        } catch (FileNotFoundException e) {
            contactList = new HashMap<>();
        }
    }

    private void loadGroupList() throws DataSaveException {
        DataSaver<HashMap<String, Group>> saver =
                new DataSaver<>(GROUP_LIST_FILE);
        try {
            groupList = saver.loadData();
        } catch (FileNotFoundException e) {
            groupList = new HashMap<>();
        }
    }

    private void saveContactList() throws DataSaveException {
//        DataSaver<HashMap<String, Contact>> saver = new DataSaver<>(CONTACT_LIST_FILE);
//        saver.saveData(contactList);
    }

    private void saveGroupList() throws DataSaveException {
//        DataSaver<HashMap<String, Group>> saver =
//                new DataSaver<>(CONTACT_LIST_FILE);
//        saver.saveData(groupList);
    }
}
