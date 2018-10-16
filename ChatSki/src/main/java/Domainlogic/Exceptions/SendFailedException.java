package Domainlogic.Exceptions;

public class SendFailedException extends Exception {
    public SendFailedException() {
        super();
    }

    public SendFailedException(String message) {
        super(message);
    }
}
