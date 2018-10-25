package DomainObjects;

import java.io.Serializable;
import java.util.Set;

public class Group implements Serializable {

    private Set<Contact> members;
    private String name;

    public Group(String name, Set<Contact> members) {
        this.name = name;
        this.members = members;
    }

    public Set<Contact> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }
}
