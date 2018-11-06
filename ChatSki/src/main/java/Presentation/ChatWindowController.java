package Presentation;

import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatWindowController {

    @FXML
    private ListView<IMessage> messageView;

    @FXML
    private ListView<ICollocutor> collocutorView;

    @FXML
    private Label collocutorName;

    @FXML
    private TextField messageField;

    @FXML
    private Button messageSendButton;

    @FXML
    private Button addContactButton;

    @FXML
    private Button addGroupButton;

    public ChatWindowController() {
        System.out.println("Controller created");
    }

    public ListView<IMessage> getMessageView() {
        return messageView;
    }

    public ListView<ICollocutor> getCollocutorView() {
        return collocutorView;
    }

    public Label getCollocutorName() {
        return collocutorName;
    }

    public TextField getMessageField() {
        return messageField;
    }

    public Button getMessageSendButton() {
        return messageSendButton;
    }

    public Button getAddContactButton() {
        return addContactButton;
    }

    public Button getAddGroupButton() {
        return addGroupButton;
    }
}
