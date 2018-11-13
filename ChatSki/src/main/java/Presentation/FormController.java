package Presentation;

import Presentation.Enums.FormType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class FormController {
    @FXML
    private Button okButton;

    @FXML
    private Button sendButton;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    @FXML
    private VBox mainPane;

    public VBox getMainPane() {
        return mainPane;
    }

    public void showCorrectButtons(FormType formType) {
        boolean okVisibility = false;
        boolean sendVisibility = false;
        boolean acceptVisibility = false;
        boolean rejectVisibility = false;
        switch (formType) {
            case OK:
                okVisibility = true;
                break;
            case SEND:
                sendVisibility = true;
                break;
            case DECISION:
                acceptVisibility = true;
                rejectVisibility = true;
                break;
        }
        okButton.setManaged(okVisibility);
        sendButton.setManaged(sendVisibility);
        acceptButton.setManaged(acceptVisibility);
        rejectButton.setManaged(rejectVisibility);
    }

    public void setOkButtonAction(Runnable action) {
        okButton.setOnAction(e -> action.run());
    }

    public void setSendButtonAction(Runnable action) {
        sendButton.setOnAction(e -> action.run());
    }

    public void setAcceptButtonAction(Runnable action) {
        acceptButton.setOnAction(e -> action.run());
    }

    public void setRejectButtonAction(Runnable action) {
        rejectButton.setOnAction(e -> action.run());
    }
}
