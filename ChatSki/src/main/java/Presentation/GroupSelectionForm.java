package Presentation;

import DomainObjects.Contact;
import Presentation.Enums.FormType;
import Presentation.Widgets.ContactSelectorController;

import java.util.List;
import java.util.Set;

public class GroupSelectionForm extends Form {

    private static final String GROUP_NAME_KEY = "group-name";

    private ContactSelectorController contactSelector;

    public GroupSelectionForm(String title, List<Contact> contacts) {
        super(title, "Please enter group information", FormType.SEND);
        addField(GROUP_NAME_KEY, "Group Name", s -> !s.trim().isEmpty(),
                "Group Name must not be empty");
        addContactSelector(contacts);
    }

    private void addContactSelector(List<Contact> contacts) {
        contactSelector = new ContactSelectorController(contacts,
                l -> l.size() > 0);
        contactSelector.setEnterAction(this::enterKeyPress);
        contactSelector.setEscAction(this::escKeyPress);
        contactSelector.setTitle("Group Members");
        contactSelector.setErrorMessage("One Contact must be selected");
        contactSelector.setListHeight(150);
        getController().getMainPane().getChildren().add(contactSelector);
    }

    @Override
    protected boolean checkForm() {
        return contactSelector.check() & super.checkForm();
    }

    public String getGroupName() {
        return getFieldText(GROUP_NAME_KEY);
    }

    public Set<Contact> getGroupMembers() {
        return contactSelector.getSelectedContacts();
    }
}
