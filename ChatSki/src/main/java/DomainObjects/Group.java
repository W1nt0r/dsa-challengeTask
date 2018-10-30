package DomainObjects;

import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IMessageSender;

import java.io.Serializable;
import java.util.Set;

public class Group implements Serializable, ICollocutor {

    private Set<Contact> members;
    private String name;

    public Group(String name, Set<Contact> members) {
        this.name = name;
        this.members = members;
    }

    public Set<Contact> getMembers() {
        return members;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(String message, IMessageSender sender) {
        sender.sendGroupMessage(this, message);
    }
}
