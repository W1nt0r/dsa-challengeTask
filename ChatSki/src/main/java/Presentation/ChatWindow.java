package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Interfaces.IMessageListener;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class ChatWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private BorderPane rootBorderPane = new BorderPane();
    private ListView<String> contactsListView = new ListView();
    private ListView<String> messageListView = new ListView();
    private Button sendButton = new Button("Send");
    private Button addContactButton = new Button("Add Contact");

    private ObservableList<String> contactItems;
    private ObservableList<String> messages = contactItems = FXCollections.observableArrayList();
    private ContactManager contactManager;
    private IMessageListener messageListener;

    @Override
    public void start(Stage stage) throws PeerCreateException, NetworkJoinException {
        rootBorderPane.setPadding(new Insets(10));

        initialize();
        initLeftPane();
        initRightPane();
        initTopPane();

        Scene scene = new Scene(rootBorderPane, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatSki");
        stage.setScene(scene);
        stage.show();
    }

    private void initialize() throws PeerCreateException, NetworkJoinException {
        contactManager = new ContactManager(KnownContacts.contacts[0], KnownContacts.getContactList());
        PeerManager.initializePeer(contactManager.getOwnContact().getName(), contactManager.getOwnContact().getState().getPort());
        PeerManager.bootstrap(new BootstrapInformation("127.0.0.1", 4000));
        messageListener = new ChatWindowMessageListener(this);
        MessageManager msgManager = new MessageManager(messageListener, contactManager);
        loadKnownContacts();
    }

    public void printReceivedMessage(String message) {
        messages.add(message);
    }

    private void loadKnownContacts() {
        contactItems = FXCollections.observableArrayList();
        contactManager.getContactList().forEach((x, y) -> {
            contactItems.add(y.getName());
        });
        contactsListView.setItems(contactItems);
    }

    private void initTopPane() {
        BorderPane topBorderPane = new BorderPane();
        topBorderPane.setLeft(new Label("Messages"));
        topBorderPane.setRight(new Label("Contacts & Groups"));
        rootBorderPane.setTop(topBorderPane);
    }

    private void initLeftPane() {

        messageListView.setItems(messages);
        sendButton.setOnMouseClicked((event) -> {
        });

        BorderPane leftBottomPane = new BorderPane();
        leftBottomPane.setRight(sendButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(messageListView);
        borderPane.setBottom(leftBottomPane);

        rootBorderPane.setLeft(borderPane);
    }

    private void initRightPane() {
        BorderPane rightBottomPane = new BorderPane();
        rightBottomPane.setRight(addContactButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(contactsListView);
        borderPane.setBottom(rightBottomPane);

        rootBorderPane.setRight(borderPane);
    }
}


