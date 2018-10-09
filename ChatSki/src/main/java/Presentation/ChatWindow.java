package Presentation;

import DomainObjects.BootstrapInformation;
import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageListener;
import Domainlogic.BootstrapManager;
import DomainObjects.Interfaces.IMessageTransmitter;
import Domainlogic.ContactManager;
import Domainlogic.Exceptions.NetworkJoinException;
import Domainlogic.Exceptions.PeerCreateException;
import Domainlogic.MessageManager;
import Domainlogic.PeerManager;
import Service.Bootstrap;
import Service.Exceptions.DataSaveException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ChatWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private BorderPane rootBorderPane = new BorderPane();
    private TableView<Contact> contactTable = new TableView<>();
    private ListView<String> messageListView = new ListView();
    private Button sendButton = new Button("Send");
    private Button addContactButton = new Button("Add Contact");
    private TextField messageField = new TextField();

    private ObservableList<Contact> contactItems;
    private ObservableList<String> messages = FXCollections.observableArrayList();
    private ContactManager contactManager;
    private MessageManager messageManager;
    private IMessageTransmitter messageListener;

    @Override
    public void start(Stage stage) throws PeerCreateException, NetworkJoinException, DataSaveException {
        rootBorderPane.setPadding(new Insets(10));

        initialize();
        initLeftPane();
        initRightPane();
        initTopPane();

        Scene scene = new Scene(rootBorderPane, 600, 400);
        stage.setResizable(false);
        stage.setTitle("ChatSki - " + contactManager.getOwnContact().getName());
        stage.setScene(scene);
        stage.show();
    }

    private void initialize() throws PeerCreateException, NetworkJoinException, DataSaveException {
        initContacts();
        initPeer();
        messageListener = new ChatWindowMessageListener(this);
        messageManager = new MessageManager(messageListener, contactManager);
        loadKnownContacts();
    }

    private void initPeer() throws DataSaveException, PeerCreateException, NetworkJoinException {
        BootstrapManager bootstrapManager = new BootstrapManager();

        if (bootstrapManager.isBootstrapInfoEmpty()) {
            bootstrapManager.setBootstrapInfo(askForBootstrapInformation());
        }

        PeerManager.initializePeer(contactManager.getOwnContact().getName(), 4001);

        while (!PeerManager.bootstrap(bootstrapManager.getBootstrapInfo())) {
            bootstrapManager.setBootstrapInfo(askForBootstrapInformation());
        }
    }

    private BootstrapInformation askForBootstrapInformation() {
        Stage form = new Stage();
        GridPane grid = new GridPane();
        grid.setBackground(Background.EMPTY);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Please enter bootstrap-information:");
        scenetitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label ipLabel = new Label("IP-Address:");
        grid.add(ipLabel, 0, 1);

        TextField ipField = new TextField();
        grid.add(ipField, 1, 1);

        Label portLabel = new Label("Port:");
        grid.add(portLabel, 0, 2);

        TextField portField = new TextField();
        grid.add(portField, 1, 2);

        Button btn = new Button("Send");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 3);
        btn.setOnAction((e) -> form.close());

        Scene scene = new Scene(grid, 300, 200);
        form.setScene(scene);

        String ipAddress;
        int port;
        do {
            form.showAndWait();
            ipAddress = ipField.getText();
            try {
                //noinspection ResultOfMethodCallIgnored
                InetAddress.getByName(ipAddress);
            } catch (UnknownHostException e) {
                ipAddress = "";
            }
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException e) {
                port = -1;
            }
        } while (ipAddress.trim().isEmpty() || port == -1);

        return new BootstrapInformation(ipAddress, port);
    }

    private void initContacts() throws DataSaveException {
        contactManager = new ContactManager();
        if (contactManager.isOwnContactEmpty()) {
            Stage form = new Stage();
            GridPane grid = new GridPane();
            grid.setBackground(Background.EMPTY);
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Text scenetitle = new Text("Please enter your name:");
            scenetitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            grid.add(scenetitle, 0, 0, 2, 1);

            TextField usernameField = new TextField();
            usernameField.setPrefWidth(450);
            grid.add(usernameField, 0, 1, 2, 1);

            Button btn = new Button("Send");
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().add(btn);
            grid.add(hbBtn, 1, 2);
            btn.setOnAction((e) -> form.close());

            Scene scene = new Scene(grid, 500, 100);
            form.setScene(scene);
            form.showAndWait();

            String username = usernameField.getText();
            while (username.trim().isEmpty()) {
                form.showAndWait();
                username = usernameField.getText();
            }

            contactManager.setOwnContactName(username);
        }
    }

    public void printReceivedMessage(String message) {
        messages.add(message);
    }

    private void loadKnownContacts() {
        contactItems = FXCollections.observableArrayList();

        TableColumn<Contact,String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("Name"));
        TableColumn<Contact, Boolean> onlineCol = new TableColumn<>("isOnline");
        onlineCol.setCellValueFactory(new PropertyValueFactory("Online"));

        contactManager.getContactList().forEach((x, y) -> {
            contactItems.add(y);
        });

        contactTable.getColumns().add(nameCol);
        contactTable.getColumns().add(onlineCol);
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
    }

    private void sendMessage() {
        try {
            messageManager.sendMessage(contactManager.getOwnContact().getName(), messageField.getText());
        } catch (Exception ex) {

        }
    }
}


