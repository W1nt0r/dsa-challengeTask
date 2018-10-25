package DomainObjects;

import DomainObjects.Interfaces.ICollocutor;

import java.io.Serializable;
import java.util.Objects;

public class Contact implements Serializable, ICollocutor {

    private String name;
    private State state;

    public Contact(String name, State state) {
        this.name = name;
        this.state = state;
    }

    public boolean isOnline(){
        return state.isOnline();
    }

    public String getIp(){
        return state.getIp();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
