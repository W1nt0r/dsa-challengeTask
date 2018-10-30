package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.*;
import javafx.application.Platform;

public class ChatWindowListener implements IMessageTransmitter,
        IStateListener, IPeerListener, ICollocutorListener {

    ChatWindow chatWindow;

    public ChatWindowListener(ChatWindow chatWindow){
        this.chatWindow = chatWindow;
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
    public void receiveContactResponseConfirmation(Contact receiver,
                                                   boolean accepted) {
        if (accepted) {
            Platform.runLater(() -> chatWindow.refreshContactList());
        }
    }

    @Override
    public void messagesUpdated(ICollocutor collocutor) {
        Platform.runLater(() -> chatWindow.updateMessages(collocutor));
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

    @Override
    public void collocutorsUpdated() {
        Platform.runLater(() -> chatWindow.refreshContactList());
    }
}
