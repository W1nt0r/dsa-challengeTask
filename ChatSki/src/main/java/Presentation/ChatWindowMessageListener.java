package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.IMessageTransmitter;
import javafx.application.Platform;

public class ChatWindowMessageListener implements IMessageTransmitter {

    ChatWindow chatWindow;

    public ChatWindowMessageListener(ChatWindow chatWindow){
        this.chatWindow = chatWindow;
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        Platform.runLater(() -> chatWindow.printReceivedMessage(sender.getName() + ": " + message));
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
