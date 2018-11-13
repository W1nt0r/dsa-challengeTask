package DomainObjects.Exceptions;

public class NotSupportedException extends RuntimeException {
    public NotSupportedException(String message) {
        super(message);
    }
}
