package DomainObjects;

import DomainObjects.Exceptions.NotSupportedException;
import DomainObjects.Interfaces.ICollocutor;
import DomainObjects.Interfaces.IMessageSender;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.Objects;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        if (!Objects.equals(name, group.name)) return false;
        if (getMembers().size() != group.getMembers().size()) return false;
        for (Contact member : getMembers()) {
            if (!group.getMembers().contains(member)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = name.hashCode();
        for (Contact member : members) {
            hashCode += member.hashCode();
        }
        return hashCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(String message, IMessageSender sender) {
        sender.sendGroupMessage(this, message);
    }

    @Override
    public void sendNotaryMessage(String message, IMessageSender sender) {
        throw new NotSupportedException("Notary Messages can't be sent to " +
                "groups");
    }
}
