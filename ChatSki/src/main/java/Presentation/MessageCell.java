package Presentation;

import DomainObjects.GroupMessage;
import DomainObjects.Interfaces.IMessage;
import DomainObjects.NotaryMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.net.URL;

public class MessageCell extends HBox {

    private static final String ACKNOWLEDGED_CLASS = "acknowledged";

    @FXML
    private Label text;

    @FXML
    private Label sender;

    public MessageCell(ListView<IMessage> parentView, IMessage message,
                       boolean own) {
        try {
            URL resource = getClass().getClassLoader().getResource("message_listitem.fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            getStyleClass().add(own ? "own-message" : "other-message");
            if (!(message instanceof GroupMessage) || own) {
                sender.setManaged(false);
            }
            if (message instanceof NotaryMessage
                    && ((NotaryMessage) message).isAcknowledged()) {
                getStyleClass().add(ACKNOWLEDGED_CLASS);
            }
            text.setText(message.getMessage());
            sender.setText(message.getSender().getName());
            updateWidth(parentView.getWidth());
            parentView.widthProperty().addListener((observable, oldValue,
                                                    newValue) -> updateWidth(newValue.doubleValue()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateWidth(double parentWidth) {
        text.setMaxWidth(parentWidth * 0.6);
    }
}
