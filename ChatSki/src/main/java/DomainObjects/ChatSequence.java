package DomainObjects;

import java.util.ArrayList;
import java.util.List;

public class ChatSequence {

    private List<Message> chatHistory;

    public ChatSequence() {
        chatHistory = new ArrayList<>();
    }

    public void appendMessage(Message message) {
        chatHistory.add(message);
    }

    public List<Message> getChatMessages() {
        return chatHistory;
    }
}
