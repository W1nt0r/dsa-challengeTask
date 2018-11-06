package Domainlogic;

import DomainObjects.Contact;
import DomainObjects.Group;
import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.ICollocutorListener;
import DomainObjects.Interfaces.IStateListener;
import DomainObjects.State;
import Service.DataSaver;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.Exceptions.ReplicationException;
import Service.StateService;

import java.io.FileNotFoundException;
import java.util.*;

public class ContactManager {

    private static final String OWN_CONTACT_FILE = "OwnContact.ser";
    private static final String CONTACT_LIST_FILE = "ContactList-%s.ser";
    private static final String GROUP_LIST_FILE = "GroupList-%s.ser";
    private final IStateListener stateListener;
    private final ICollocutorListener collocutorListener;

    private Contact ownContact;

    private HashSet<Contact> contactList;
    private HashSet<Group> groupList;

    public ContactManager(IStateListener stateListener,
                          ICollocutorListener collocutorListener) throws DataSaveException {
        this.stateListener = stateListener;
        this.collocutorListener = collocutorListener;
        loadOwnContact();
        loadListData();
    }

    private void loadListData() throws DataSaveException {
        if (!isOwnContactEmpty()) {
            loadContactList();
            loadGroupList();
        } else {
            contactList = new HashSet<>();
            groupList = new HashSet<>();
        }
    }

    public List<ICollocutor> getCollocutors() {
        ArrayList<ICollocutor> collocutors = new ArrayList<>();
        collocutors.addAll(contactList);
        collocutors.addAll(groupList);
        return collocutors;
    }

    public List<Contact> getContactList() {
        return new ArrayList<>(contactList);
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
        loadListData();
    }

    public void addContact(String contactName) throws DataSaveException {
        Contact newContact = createContactFromName(contactName);
        contactList.add(newContact);
        saveContactList();
        try {
            updateState(newContact);
        } catch (PeerNotInitializedException e) {
            stateListener.showThrowable(e);
        }
        collocutorListener.collocutorsUpdated();
    }

    public void addGroup(Group newGroup) throws DataSaveException {
        groupList.add(newGroup);
        saveGroupList();
        collocutorListener.collocutorsUpdated();
    }

    public boolean isKnownGroup(Group group) {
        return groupList.contains(group);
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
        for (Contact contact : contactList) {
            updateState(contact);
        }
    }

    private void updateState(
            Contact contact) throws PeerNotInitializedException {
        StateService.LoadStateFromDht(contact.getName(), state -> {
            contact.setState(state);
            stateListener.updateContactState(contact);
        }, stateListener::showThrowable);
    }

    public void updateWithReceivedState(Contact contact) {
        contactList.remove(contact);
        contactList.add(contact);
        stateListener.updateContactState(contact);
    }

    private String generateFileName(String template) {
        return String.format(template, ownContact.getName());
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
        DataSaver<HashSet<Contact>> saver =
                new DataSaver<>(generateFileName(CONTACT_LIST_FILE));
        try {
            contactList = saver.loadData();
        } catch (FileNotFoundException e) {
            contactList = new HashSet<>();
        }
    }

    private void loadGroupList() throws DataSaveException {
        DataSaver<HashSet<Group>> saver =
                new DataSaver<>(generateFileName(GROUP_LIST_FILE));
        try {
            groupList = saver.loadData();
        } catch (FileNotFoundException e) {
            groupList = new HashSet<>();
        }
    }

    private void saveContactList() throws DataSaveException {
        DataSaver<HashSet<Contact>> saver =
                new DataSaver<>(generateFileName(CONTACT_LIST_FILE));
        saver.saveData(contactList);
    }

    private void saveGroupList() throws DataSaveException {
        DataSaver<HashSet<Group>> saver =
                new DataSaver<>(generateFileName(GROUP_LIST_FILE));
        saver.saveData(groupList);
    }
}
