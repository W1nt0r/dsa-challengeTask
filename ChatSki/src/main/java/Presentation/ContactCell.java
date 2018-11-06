package Presentation;

import DomainObjects.Contact;
import DomainObjects.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class ContactCell extends VBox {

    @FXML
    private Label contactName;

    @FXML
    private Label contactStatus;

    public ContactCell() {
        try {
            URL resource = getClass().getClassLoader().getResource("contact_listitem.fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setContact(Contact contact) {
        contactName.setText(contact.getName());
        State state = contact.getState();
        String styleClass = (state != null && state.isOnline()) ? "online" :
                "offline";
        String text = (state != null && state.isOnline()) ? "Online" :
                "Offline";
        contactStatus.getStyleClass().add(styleClass);
        contactStatus.setText(text);
    }
}
