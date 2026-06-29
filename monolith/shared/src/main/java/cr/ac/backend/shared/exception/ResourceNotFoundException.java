package cr.ac.backend.shared.exception;

public class ResourceNotFoundException extends GymException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatus() {
        return 404;
    }
}
