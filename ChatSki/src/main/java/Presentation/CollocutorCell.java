package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.ICollocutor;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

public class CollocutorCell extends ListCell<ICollocutor> {

    @Override
    protected void updateItem(ICollocutor item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            if (item instanceof Contact) {
                ContactCell cell = new ContactCell();
                cell.setContact((Contact) item);
                setGraphic(cell);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }
}
