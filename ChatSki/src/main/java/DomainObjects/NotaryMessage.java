package DomainObjects;

import DomainObjects.Interfaces.IMessage;
import DomainObjects.Interfaces.IMessageListener;
import DomainObjects.Interfaces.ITransmittable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class NotaryMessage implements Serializable, ITransmittable, IMessage {

    private Contact sender;
    private String message;
    private Long timestamp;
    private boolean acknowledged;

    public NotaryMessage(Contact sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    @Override
    public Contact getSender() {
        return sender;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void handleReception(IMessageListener listener) {
        listener.receiveNotaryMessage(sender, this);
    }

    @Override
    public void handleConfirmation(Contact receiver,
                                   IMessageListener listener) {
        listener.receiveNotaryMessageConfirmation(receiver, this);
    }

    @Override
    public String toString(){
        return sender.toString() + message + timestamp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaryMessage that = (NotaryMessage) o;
        return Objects.equals(sender, that.sender) &&
                Objects.equals(message, that.message) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, message, timestamp);
    }
}
