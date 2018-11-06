package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessage;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MessageCellFactory implements Callback<ListView<IMessage>,
        ListCell<IMessage>> {

    private Contact ownContact;

    public MessageCellFactory(Contact ownContact) {
        this.ownContact = ownContact;
    }

    @Override
    public ListCell<IMessage> call(ListView<IMessage> param) {
        return new MessageCellItem(param, ownContact);
    }
}
