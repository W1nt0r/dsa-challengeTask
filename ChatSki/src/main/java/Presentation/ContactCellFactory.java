package Presentation;

import DomainObjects.Interfaces.ICollocutor;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ContactCellFactory implements Callback<ListView<ICollocutor>, ListCell<ICollocutor>> {

    @Override
    public ListCell<ICollocutor> call(ListView<ICollocutor> param) {
        return new CollocutorCell();
    }
}
