package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageTransmitter;
import DomainObjects.Interfaces.IPeerListener;
import DomainObjects.Interfaces.IStateListener;
import DomainObjects.Message;
import javafx.application.Platform;

public class ChatWindowListener implements IMessageTransmitter,
        IStateListener, IPeerListener {

    ChatWindow chatWindow;

    public ChatWindowListener(ChatWindow chatWindow){
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
    public void receiveMessageConfirmation(Contact receiver, Message message) {
        Platform.runLater(() -> chatWindow.printReceivedMessage(message));
    }

    @Override
    public void receiveContactRequestConfirmation(Contact receiver) {

    }

    @Override
    public void receiveContactResponseConfirmation(Contact receiver,
                                                   boolean accepted) {
        if (accepted) {
            Platform.runLater(() -> chatWindow.refreshContactList());
        }
    }

    @Override
    public void showThrowable(Throwable t) {
        Platform.runLater(() -> chatWindow.showThrowable(t));
    }

    @Override
    public void replicationFinished(String stateId) {
        Platform.runLater(() -> chatWindow.replicationFinished(stateId));
    }

    @Override
    public void updateContactState(Contact contact) {
        Platform.runLater(() -> chatWindow.refreshContactList());
    }

    @Override
    public void peerClosed() {
        Platform.runLater(() -> chatWindow.closeApplication());
    }
}
