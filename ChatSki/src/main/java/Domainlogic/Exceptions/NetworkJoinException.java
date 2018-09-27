package Domainlogic.Exceptions;

public class NetworkJoinException extends Exception {

    public NetworkJoinException(Exception innerException) {
        super(innerException);
    }
}
