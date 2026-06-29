package cr.ac.backend.shared.exception;

public abstract class GymException extends RuntimeException {

    private final String field;

    public GymException(String message) {
        super(message);
        this.field = null;
    }

    public GymException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public abstract int getHttpStatus();
}
