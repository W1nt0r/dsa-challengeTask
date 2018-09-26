package DomainObjects;

import java.io.Serializable;

public class Message implements Serializable {
    private final Contact sender;
    private final String message;

    public Message(Contact sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public Contact getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
