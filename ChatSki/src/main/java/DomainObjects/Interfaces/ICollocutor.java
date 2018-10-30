package DomainObjects.Interfaces;

public interface ICollocutor {
    void sendMessage(String message, IMessageSender sender);
    String getName();
}
