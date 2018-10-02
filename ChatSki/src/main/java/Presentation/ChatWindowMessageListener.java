package Presentation;

import DomainObjects.Contact;
import DomainObjects.Interfaces.IMessageListener;

public class ChatWindowMessageListener implements IMessageListener {

    ChatWindow chatWindow;

    public ChatWindowMessageListener(ChatWindow chatWindow){
        this.chatWindow = chatWindow;
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        chatWindow.printReceivedMessage(sender.getName() + ": " + message);
    }

    @Override
    public void receiveContactRequest(Contact sender) {

    }

    @Override
    public void receiveContactResponse(Contact sender, boolean accepted) {

    }
}
