package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageTransmitter;
import DomainObjects.Message;
import javafx.application.Platform;

public class ChatWindowMessageListener implements IMessageTransmitter {

    ChatWindow chatWindow;

    public ChatWindowMessageListener(ChatWindow chatWindow){
        this.chatWindow = chatWindow;
    }

    @Override
    public void receiveMessage(Contact sender, Message message) {
        Platform.runLater(() -> chatWindow.printReceivedMessage(message));
    }

    @Override
    public void receiveContactRequest(Contact sender) {
        Platform.runLater(() -> chatWindow.showContactRequest(sender));
    }

    @Override
    public void receiveContactResponse(Contact sender, boolean accepted) {
        Platform.runLater(() -> chatWindow.showContactResponse(sender, accepted));
    }

    @Override
    public void showException(Exception e) {
        chatWindow.showException(e);
    }
}
