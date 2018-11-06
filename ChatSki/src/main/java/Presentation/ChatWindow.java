package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IMessage;
import DomainObjects.State;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;


public class ChatWindow extends Application {

    private final static String SHUTDOWN_STATE = "shutdown-state";
    private final static String STARTUP_STATE = "startup-state";

    private ChatWindowController controller;
    private Stage stage;
    private Scene currentScene;
    private ObservableList<ICollocutor> collocutorItems;
    private ObservableList<IMessage> messages;
    private ContactManager contactManager;
    private MessageManager messageManager;
    private ChatWindowListener chatWindowListener;
    private ICollocutor activeChat;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        collocutorItems = FXCollections.observableArrayList();
        messages = FXCollections.observableArrayList();
        chatWindowListener = new ChatWindowListener(this);

        boolean initialized = false;
        try {
            initialized = initialize();
        } catch (Exception e) {
            showThrowable(e);
        }

        if (!initialized) {
            return;
        }

        initStage();
        loadKnownContacts();
        stage.show();
        stage.setOnCloseRequest(e -> {
            showShutDown();
            e.consume();
        });
    }

    private void initStage() {
        Parent root;
        try {
            URL resource = getClass().getClassLoader().getResource("chat_window.fxml");
            if (resource != null) {
                FXMLLoader loader = new FXMLLoader(resource);
                root = loader.load();
                controller = loader.getController();

                currentScene = new Scene(root);
                stage.setScene(currentScene);
                stage.setTitle("ChatSki - " + contactManager.getOwnContact().getName());
                controller.getMessageSendButton().setOnAction(e -> sendMessage());
                controller.getAddContactButton().setOnAction(e -> addContact());
                controller.getAddGroupButton().setOnAction(e -> addGroup());
                controller.getMessageField().setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        sendMessage();
                    }
                });
                controller.getCollocutorView().getSelectionModel()
                        .selectedItemProperty().addListener((observable, oldSelected, newSelected) -> showConversation(newSelected));
                controller.getCollocutorView().setItems(collocutorItems);
                controller.getMessageView().setItems(messages);
                controller.getCollocutorView().setCellFactory(new ContactCellFactory());
                controller.getMessageView().setCellFactory(new MessageCellFactory(contactManager.getOwnContact()));
            } else {
                throw new FileNotFoundException("chat_window.fxml not found");
            }
        } catch (IOException e) {
            showThrowable(e);
        }
    }

    private void showShutDown() {
        BorderPane pane = new BorderPane();
        pane.setBackground(Background.EMPTY);
        Label shuttingDown = new Label("Shutting down...");
        shuttingDown.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 24));
        pane.setCenter(shuttingDown);
        double height = currentScene.getHeight();
        double width = currentScene.getWidth();
        currentScene = new Scene(pane, width, height);
        stage.setScene(currentScene);
        try {
            messageManager.updateOwnState(SHUTDOWN_STATE, false);
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
        messageManager.updateOwnState(STARTUP_STATE, true);
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

    private boolean initPeer() throws DataSaveException, PeerCreateException, NetworkJoinException, IOException {
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

            if (activeChat == null) {
                activeChat = contactManager.getContactList().get(0);
            }
        }
        showInformation("Request confirmation",
                sender.getName() + " " + (accepted ? "accepted" : "rejected") + " your request");
    }

    private void loadKnownContacts() {
        collocutorItems.clear();
        List<ICollocutor> collocutors = contactManager.getCollocutors();
        collocutors.sort(Comparator.comparing(c -> c.getName().toLowerCase()));
        collocutorItems.addAll(collocutors);
    }

    private void showConversation(ICollocutor collocutor) {
        if (collocutor != null) {
            messages.clear();
            activeChat = collocutor;

            List<IMessage> conversation =
                    messageManager.getChatHistory(collocutor);
            messages.addAll(conversation);
            controller.getMessageView().scrollTo(conversation.get(conversation.size() - 1));
            controller.getCollocutorName().setText(collocutor.getName());
        }
    }

    private void sendMessage() {
        try {
            if (activeChat != null) {
                String messageText = controller.getMessageField().getText();
                controller.getMessageField().clear();
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
        if (collocutor.equals(activeChat)) {
            showConversation(collocutor);
        }
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
