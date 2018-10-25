package Presentation.Widgets;

import DomainObjects.Contact;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactSelector extends ListView<Contact> {

    private ObservableList<Contact> contacts;

    public ContactSelector(List<Contact> contacts) {
        this.contacts = FXCollections.observableArrayList();
        this.contacts.addAll(contacts);
        this.setItems(this.contacts);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public Set<Contact> getSelectedContacts() {
        ObservableList<Contact> selectedContacts =
                getSelectionModel().getSelectedItems();
        return new HashSet<>(selectedContacts);
    }
}
