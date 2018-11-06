package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessage;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class MessageCellItem extends ListCell<IMessage> {

    private final Contact ownContact;
    private final ListView<IMessage> parentView;

    public MessageCellItem(ListView<IMessage> parentView, Contact ownContact) {
        this.ownContact = ownContact;
        this.parentView = parentView;
    }

    @Override
    protected void updateItem(IMessage item, boolean empty) {
        super.updateItem(item, empty);

        setStyle("-fx-background-color: #ffffff");
        if (empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        } else {
            MessageCell cell = new MessageCell(parentView, item,
                    item.getSender().equals(ownContact));
            setGraphic(cell);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

}
