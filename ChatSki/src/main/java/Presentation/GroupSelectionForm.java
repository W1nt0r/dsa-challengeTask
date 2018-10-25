package Presentation;

import DomainObjects.Contact;
import Presentation.Widgets.ContactSelector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;

public class GroupSelectionForm {

    private static final Font TEXT_FONT = Font.font("Segoe UI",
            FontWeight.NORMAL, 16);
    private static final Font ERROR_FONT = Font.font("Segoe UI",
            FontWeight.NORMAL, 14);
    private static final String ERROR_COLOR = "#FF0000";

    private VBox container;
    private Stage form;
    private TextField groupNameField;
    private ContactSelector contactSelector;
    private Label contactSelectorError;
    private Label groupNameError;
    private boolean sent;

    public GroupSelectionForm(String title, List<Contact> contacts) {
        form = new Stage();
        container = new VBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setBackground(Background.EMPTY);
        container.setPadding(new Insets(25));
        Scene scene = new Scene(container);
        form.setTitle(title);
        form.setScene(scene);
        form.setResizable(false);

        addGroupNameField();
        addContactSelector(contacts);
        addSendButton();
    }

    private void addContactSelector(List<Contact> contacts) {
        VBox groupMembersGroup = new VBox();
        Label groupMembersLabel = new Label("Group Members");
        groupMembersLabel.setFont(TEXT_FONT);
        groupMembersGroup.getChildren().add(groupMembersLabel);
        contactSelector = new ContactSelector(contacts);
        contactSelector.setPrefHeight(100);
        contactSelector.setOnKeyPressed(this::keyPressed);
        groupMembersGroup.getChildren().add(contactSelector);
        contactSelectorError = new Label("At least on contact must be " +
                "selected");
        contactSelectorError.setFont(ERROR_FONT);
        contactSelectorError.setTextFill(Paint.valueOf(ERROR_COLOR));
        groupMembersGroup.getChildren().add(contactSelectorError);
        container.getChildren().add(groupMembersGroup);
    }

    private void addGroupNameField() {
        VBox groupNameGroup = new VBox();
        Label groupNameLabel = new Label("Group Name");
        groupNameLabel.setFont(TEXT_FONT);
        groupNameGroup.getChildren().add(groupNameLabel);
        groupNameField = new TextField();
        groupNameField.setFont(TEXT_FONT);
        groupNameField.setOnKeyPressed(this::keyPressed);
        groupNameLabel.setPrefWidth(300);
        groupNameGroup.getChildren().add(groupNameField);
        groupNameError = new Label("Group Name must not be empty");
        groupNameError.setFont(ERROR_FONT);
        groupNameError.setTextFill(Paint.valueOf(ERROR_COLOR));
        groupNameGroup.getChildren().add(groupNameError);
        container.getChildren().add(groupNameGroup);
    }

    private void addSendButton() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        Button sendButton = new Button("Send");
        sendButton.setFont(TEXT_FONT);
        sendButton.setAlignment(Pos.BOTTOM_RIGHT);
        sendButton.setOnAction(e -> sendForm());
        buttonBox.getChildren().add(sendButton);
        container.getChildren().add(buttonBox);
    }

    private boolean checkForm() {
        boolean noMissing = true;
        if (getGroupName().trim().isEmpty()) {
            groupNameError.setVisible(true);
            noMissing = false;
        }
        if (getGroupMembers().size() == 0) {
            contactSelectorError.setVisible(true);
            noMissing = false;
        }
        return noMissing;
    }

    private void hideErrorFields() {
        groupNameError.setVisible(false);
        contactSelectorError.setVisible(false);
    }

    private void sendForm() {
        if (checkForm()) {
            sent = true;
            form.close();
        }
    }

    private void keyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            sendForm();
        }
    }

    public String getGroupName() {
        return groupNameField.getText();
    }

    public Set<Contact> getGroupMembers() {
        return contactSelector.getSelectedContacts();
    }

    public boolean showAndWait() {
        sent = false;
        hideErrorFields();
        form.showAndWait();
        return sent;
    }
}
