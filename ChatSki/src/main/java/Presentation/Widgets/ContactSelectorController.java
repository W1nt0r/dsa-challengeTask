package Presentation.Widgets;

import DomainObjects.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ContactSelectorController extends VBox {

    private static final String STYLE_CLASS_FOCUSED = "focused";
    private static final String STYLE_CLASS_ERROR = "error";

    @FXML
    private Label title;

    @FXML
    private ListView<Contact> contactList;

    @FXML
    private Label error;

    private Predicate<Set<Contact>> checkMethod;
    private Runnable escAction;
    private Runnable enterAction;

    public ContactSelectorController(List<Contact> contacts,
                                     Predicate<Set<Contact>> checkMethod) {
        this.checkMethod = checkMethod;
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getClassLoader().getResource("contact_selection.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        contactList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<Contact> observableContacts =
                FXCollections.observableList(contacts);
        contactList.setItems(observableContacts);

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                contactList.requestFocus();
            }
        });

        contactList.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getStyleClass().add(STYLE_CLASS_FOCUSED);
            } else {
                getStyleClass().remove(STYLE_CLASS_FOCUSED);
            }
        });

        contactList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && escAction != null) {
                escAction.run();
            } else if (event.getCode() == KeyCode.ENTER && enterAction != null) {
                enterAction.run();
            }
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setErrorMessage(String errorMessage) {
        error.setText(errorMessage);
    }

    public void setEnterAction(Runnable enterAction) {
        this.enterAction  = enterAction;
    }

    public void setEscAction(Runnable escAction) {
        this.escAction = escAction;
    }

    public boolean check() {
        if (!checkMethod.test(getSelectedContacts())) {
            getStyleClass().add(STYLE_CLASS_ERROR);
            return false;
        }
        return true;
    }

    public Set<Contact> getSelectedContacts() {
        ObservableList<Contact> selectedContacts =
                contactList.getSelectionModel().getSelectedItems();
        return new HashSet<>(selectedContacts);
    }

    public void setListHeight(double value) {
        contactList.setPrefHeight(value);
    }
}
