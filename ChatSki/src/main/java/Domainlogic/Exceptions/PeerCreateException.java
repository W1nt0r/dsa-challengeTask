package Domainlogic.Exceptions;

public class PeerCreateException extends Exception {
    public PeerCreateException(Exception innerException) {
        super(innerException);
    }
}
