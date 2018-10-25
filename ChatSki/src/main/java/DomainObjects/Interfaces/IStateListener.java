package DomainObjects.Interfaces;

import DomainObjects.Contact;

public interface IStateListener {
    void updateContactState(Contact contact);

    void showThrowable(Throwable t);

    void replicationFinished(String stateId);
}
