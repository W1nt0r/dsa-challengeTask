package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import DomainObjects.Message;
import DomainObjects.State;
import Domainlogic.BootstrapManager;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.Exceptions.SendFailedException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Presentation.Enums.FormType;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.PortFinder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class ChatWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private BorderPane rootBorderPane = new BorderPane();
    private TableView<Contact> contactTable = new TableView<>();
    private ListView<Message> messageListView = new ListView<>();
    private Button sendButton = new Button("Send");
    private Button addContactButton = new Button("Add Contact");
    private TextField messageField = new TextField();

    private ObservableList<Contact> contactItems = FXCollections.observableArrayList();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private ContactManager contactManager;
    private MessageManager messageManager;
    private Contact activeChat;

    @Override
    public void start(Stage stage) {
        rootBorderPane.setPadding(new Insets(10));

        boolean initialized = false;
        try {
            initialized = initialize();
        } catch (Exception e) {
            showException(e);
        }

        if (!initialized) {
            stop();
            return;
        }

        initLeftPane();
        initRightPane();
        initTopPane();

        Scene scene = new Scene(rootBorderPane, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatSki - " + contactManager.getOwnContact().getName());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        PeerManager.closePeer();
    }

    private boolean initialize() throws PeerCreateException, NetworkJoinException, DataSaveException, PeerNotInitializedException, IOException {
        if (!initContacts() || !initPeer()) {
            return false;
        }
        messageManager = new MessageManager(new ChatWindowMessageListener(this), contactManager);
        loadKnownContacts();

        initializeActiveChat();

        return true;
    }

    private void initializeActiveChat() {
        List<Contact> contactList = contactManager.getContactList();
        if (contactList.size() > 0) {
            activeChat = contactList.get(0);
        } else {
            activeChat = null;
        }
    }

    private boolean initPeer() throws DataSaveException, PeerCreateException, NetworkJoinException, IOException, PeerNotInitializedException {
        BootstrapManager bootstrapManager = new BootstrapManager();

        if (bootstrapManager.isBootstrapInfoEmpty()) {
            BootstrapInformation info = askForBootstrapInformation(null);
            if (info == null) {
                return false;
            }
            bootstrapManager.setBootstrapInfo(info);
        }

        int port = PortFinder.findFreePort();
        String ip = PeerManager.initializePeer(contactManager.getOwnContact().getName(), port).substring(1);
        contactManager.getOwnContact().setState(new State(ip, port, true));

        while (!PeerManager.bootstrap(bootstrapManager.getBootstrapInfo())) {
            BootstrapInformation info = askForBootstrapInformation(bootstrapManager.getBootstrapInfo());
            if (info == null) {
                return false;
            }
            bootstrapManager.setBootstrapInfo(info);
        }
        contactManager.writeOwnStateToDHT(true);
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private BootstrapInformation askForBootstrapInformation(
            BootstrapInformation oldInformation) {
        String oldIp = "";
        String oldPort = "";
        if (oldInformation != null) {
            oldIp = oldInformation.getIpAddress();
            oldPort = String.valueOf(oldInformation.getPort());
        }

        Form form = new Form("Bootstrap-Information", "Please enter the " +
                "Bootstrap information", FormType.SEND);
        form.addField("ip", "IP-Address", oldIp, s -> {
            try {
                InetAddress.getByName(s);
                return true;
            } catch (UnknownHostException e) {
                return false;
            }
        }, "IP-Address must be valid");

        form.addField("port", "Port", oldPort, s -> {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }, "Port must be a number");

        if (!form.showAndWait()) {
            return null;
        } else {
            String ipAddress = form.getFieldText("ip");
            int port = Integer.parseInt(form.getFieldText("port"));
            return new BootstrapInformation(ipAddress, port);
        }
    }

    private boolean initContacts() throws DataSaveException {
        contactManager = new ContactManager();
        if (contactManager.isOwnContactEmpty()) {
            Form form = new Form("Username", "Please enter your username", FormType.SEND);
            form.addField("username", "Username", s -> !s.trim().isEmpty(),
                    "Username must not be empty");

            boolean result = form.showAndWait();

            if (!result) {
                return false;
            }

            contactManager.setOwnContactName(form.getFieldText("username"));
        }
        return true;
    }

    public void printReceivedMessage(Message message) {
        messages.add(message);
    }

    public void showContactRequest(Contact sender) {
        Form form = new Form("Contact-Request", sender.getName() + " sent you" +
                " a contact-request", FormType.DECISION);
        boolean accepted = form.showAndWait();
        try {
            boolean success = messageManager.sendContactResponse(sender, accepted);
            if (success && accepted) {
                loadKnownContacts();
            }
        } catch (SendFailedException | PeerNotInitializedException e) {
            showException(e);
        }
    }

    public void showContactResponse(Contact sender, boolean accepted) {
        loadKnownContacts();
        showInformation("Request confirmation",
                sender.getName() + " " + (accepted ? "accepted" : "rejected") + " your request");
    }

    private void loadKnownContacts() {
        contactManager.updateStates();
        contactItems.clear();

        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        TableColumn<Contact, Boolean> onlineCol = new TableColumn<>("isOnline");
        onlineCol.setCellValueFactory(new PropertyValueFactory<>("Online"));

        TableColumn<Contact, String> ipCol = new TableColumn<>("IP");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("Ip"));

        contactItems.addAll(contactManager.getContactList());

        contactTable = new TableView<>();
        contactTable.getColumns().add(nameCol);
        contactTable.getColumns().add(onlineCol);
        contactTable.getColumns().add(ipCol);
        contactTable.setItems(contactItems);
    }

    private void initTopPane() {
        BorderPane topBorderPane = new BorderPane();
        topBorderPane.setLeft(new Label("Messages"));
        topBorderPane.setRight(new Label("Contacts & Groups"));
        rootBorderPane.setTop(topBorderPane);
    }

    private void initLeftPane() {
        messageListView.setItems(messages);
        sendButton.setOnMouseClicked((event) -> sendMessage());
        addContactButton.setOnAction((event) -> addContact());

        BorderPane leftBottomPane = new BorderPane();
        leftBottomPane.setRight(sendButton);
        leftBottomPane.setLeft(messageField);

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
        borderPane.setCenter(contactTable);
        borderPane.setBottom(rightBottomPane);

        rootBorderPane.setRight(borderPane);

        contactTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldSelected, newSelected) -> showContactConversation(newSelected));
    }

    private void showContactConversation(Contact contact) {
        if (contact != null) {
            messages.clear();
            activeChat = contact;

            String username = contact.getName();
            List<Message> conversation = messageManager.getChatHistory(username);
            messages.addAll(conversation);
        }
    }

    private void sendMessage() {
        try {
            if (activeChat != null) {
                messageManager.sendMessage(activeChat.getName(), messageField.getText());
            } else {
                showInformation("No Contacts available", "You should add some contacts to your contact list.");
            }


        } catch (Exception ex) {
            showException(ex);
        }
    }

    private void addContact() {
        Form newForm = new Form("Add Contact", "Please enter the username of " +
                "the contact", FormType.SEND);
        newForm.addField("username", "username", s -> !s.trim().isEmpty(),
                "Username must not be empty");
        boolean result = newForm.showAndWait();

        if (result) {
            try {
                messageManager.sendContactRequest(newForm.getFieldText(
                        "username"));
            } catch (PeerNotInitializedException | SendFailedException e) {
                showException(e);
            }
        }
    }

    public void showException(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "ChatSki threw the following exception:\n" + e.getMessage(), ButtonType.OK);
        alert.showAndWait();
        e.printStackTrace();
    }

    private void showInformation(String title, String message) {
        Form form = new Form(title, message, FormType.OK);
        form.showAndWait();
    }
}
