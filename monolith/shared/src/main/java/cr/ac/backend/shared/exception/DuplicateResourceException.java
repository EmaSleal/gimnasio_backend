package cr.ac.backend.shared.exception;

public class DuplicateResourceException extends GymException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, String field) {
        super(message, field);
    }

    @Override
    public int getHttpStatus() {
        return 409;
    }
}
