package DomainObjects;

import java.io.Serializable;

public class Contact implements Serializable {

    private String name;
    private State state;

    public Contact(String name, State state) {
        this.name = name;
        this.state = state;
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
}
