package DomainObjects;

import DomainObjects.Interfaces.IMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatSequence {

    private List<IMessage> chatHistory;

    public ChatSequence() {
        chatHistory = new ArrayList<>();
    }

    public void appendMessage(IMessage message) {
        chatHistory.add(message);
    }

    public List<IMessage> getChatMessages() {
        return chatHistory;
    }
}
