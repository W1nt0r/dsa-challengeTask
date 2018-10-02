package Presentation;

import DomainObjects.Contact;
import Service.IMessageListener;

public class ChatWindowMessageListener implements IMessageListener {

    ChatWindow chatWindow;

    public ChatWindowMessageListener(ChatWindow chatWindow){
        this.chatWindow = chatWindow;
    }

    @Override
    public void receiveMessage(Contact sender, String message) {
        chatWindow.printReceivedMessage(sender.getName() + ": " + message);
    }
}
