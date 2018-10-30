package Presentation;

import DomainObjects.*;
import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IMessage;
import Domainlogic.BootstrapManager;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Presentation.Enums.FormType;
import Service.Exceptions.DataSaveException;
import Service.Exceptions.PeerNotInitializedException;
import Service.Exceptions.ReplicationException;
import Service.PortFinder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class ChatWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final static String SHUTDOWN_STATE = "shutdown-state";
    private final static String STARTUP_STATE = "startup-state";

    private Stage stage;
    private BorderPane rootBorderPane = new BorderPane();
    private ListView<ICollocutor> contactTable = new ListView<>();
    private ListView<IMessage> messageListView = new ListView<>();
    private Button sendButton = new Button("Send");
    private Button addGroupButton = new Button("Add Group");
    private Button addContactButton = new Button("Add Contact");
    private TextField messageField = new TextField();
    private ChatWindowListener chatWindowListener =
            new ChatWindowListener(this);

    private ObservableList<ICollocutor> contactItems = FXCollections.observableArrayList();
    private ObservableList<IMessage> messages = FXCollections.observableArrayList();
    private ContactManager contactManager;
    private MessageManager messageManager;
    private ICollocutor activeChat;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        rootBorderPane.setPadding(new Insets(10));

        boolean initialized = false;
        try {
            initialized = initialize();
        } catch (Exception e) {
            showThrowable(e);
        }

        if (!initialized) {
            return;
        }

        initLeftPane();
        initRightPane();
        initTopPane();

        Scene scene = new Scene(rootBorderPane, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatSki - " + contactManager.getOwnContact().getName());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            showShutDown();
            e.consume();
        });
        stage.show();
    }

    private void clearRootBorderPane() {
        rootBorderPane.setTop(null);
        rootBorderPane.setLeft(null);
        rootBorderPane.setBottom(null);
        rootBorderPane.setRight(null);
        rootBorderPane.setCenter(null);
    }

    private void showShutDown() {
        clearRootBorderPane();
        Label shutDownLabel = new Label("Shutting down...");
        shutDownLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        rootBorderPane.setCenter(shutDownLabel);

        try {
            contactManager.writeOwnStateToDHT(SHUTDOWN_STATE, false);
        } catch (PeerNotInitializedException | ReplicationException e) {
            showThrowable(e);
        }
    }

    private void close() {
        if (PeerManager.isPeerInitialized()) {
            PeerManager.closePeer(chatWindowListener);
        } else {
            closeApplication();
        }
    }

    public void closeApplication() {
        stage.close();
    }

    private boolean initialize() throws PeerCreateException, NetworkJoinException, DataSaveException, PeerNotInitializedException, IOException, ReplicationException {
        if (!initContacts() || !initPeer()) {
            return false;
        }
        messageManager = new MessageManager(chatWindowListener, chatWindowListener,
                contactManager);
        loadKnownContacts();
        contactManager.updateStates();

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

    private boolean initPeer() throws DataSaveException, PeerCreateException, NetworkJoinException, IOException, PeerNotInitializedException, ReplicationException {
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
        contactManager.writeOwnStateToDHT(STARTUP_STATE, true);
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
        contactManager = new ContactManager(chatWindowListener, chatWindowListener);
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

    public void showContactRequest(Contact sender) {
        Form form = new Form("Contact-Request", sender.getName() + " sent you" +
                " a contact-request", FormType.DECISION);
        boolean accepted = form.showAndWait();
        try {
            messageManager.sendContactResponse(sender, accepted);
        } catch (PeerNotInitializedException e) {
            showThrowable(e);
        }
    }

    public void showContactResponse(Contact sender, boolean accepted) {
        if (accepted) {
            loadKnownContacts();
        }
        showInformation("Request confirmation",
                sender.getName() + " " + (accepted ? "accepted" : "rejected") + " your request");
    }

    private void loadKnownContacts() {
        contactItems.clear();

//        TableColumn<Contact, String> nameCol = new TableColumn<>("Name");
//        nameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
//        TableColumn<Contact, Boolean> onlineCol = new TableColumn<>("isOnline");
//        onlineCol.setCellValueFactory(new PropertyValueFactory<>("Online"));
//
//        TableColumn<Contact, String> ipCol = new TableColumn<>("IP");
//        ipCol.setCellValueFactory(new PropertyValueFactory<>("Ip"));

        contactItems.addAll(contactManager.getCollocutors());

//        contactTable = new TableView<>();
//        contactTable.getColumns().add(nameCol);
//        contactTable.getColumns().add(onlineCol);
//        contactTable.getColumns().add(ipCol);
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
        addGroupButton.setOnAction((event) -> addGroup());

        messageField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                sendMessage();
            }
        });

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
        HBox rightBottomPane = new HBox(10);
        rightBottomPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightBottomPane.getChildren().add(addGroupButton);
        rightBottomPane.getChildren().add(addContactButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setCenter(contactTable);
        borderPane.setBottom(rightBottomPane);

        rootBorderPane.setRight(borderPane);

        contactTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldSelected, newSelected) -> showConversation(newSelected));

        contactTable.setCellFactory(new ContactCellFactory());
    }

    private void showConversation(ICollocutor collocutor) {
        if (collocutor != null) {
            messages.clear();
            activeChat = collocutor;

            String username = collocutor.getName();
            List<IMessage> conversation = messageManager.getChatHistory(username);
            messages.addAll(conversation);
        }
    }

    private void sendMessage() {
        try {
            if (activeChat != null) {
                String messageText = messageField.getText();
                messageField.clear();
                activeChat.sendMessage(messageText, messageManager);
            } else {
                showInformation("No Contacts chosen", "Please chose a " +
                        "contact or add a new one.");
            }
        } catch (Exception ex) {
            showThrowable(ex);
        }
    }

    private void addGroup() {
        GroupSelectionForm form = new GroupSelectionForm("Add group", contactManager.getContactList());
        if (form.showAndWait()) {
            messageManager.sendGroupCreation(form.getGroupName(), form.getGroupMembers());
            loadKnownContacts();
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
            } catch (PeerNotInitializedException e) {
                showThrowable(e);
            }
        }
    }

    public void updateMessages(ICollocutor collocutor) {
        showConversation(collocutor);
    }

    public void showThrowable(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "ChatSki threw the following exception:\n" + t.getMessage(), ButtonType.OK);
        alert.showAndWait();
        t.printStackTrace();
    }

    public void refreshContactList() {
        loadKnownContacts();
    }

    public void replicationFinished(String stateId) {
        System.out.println("replication finished " + stateId);
        if (stateId.equals(SHUTDOWN_STATE)) {
            close();
        }
    }

    private void showInformation(String title, String message) {
        Form form = new Form(title, message, FormType.OK);
        form.showAndWait();
    }
}
