package cr.ac.backend.shared.exception;

public class ForbiddenException extends GymException {

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatus() {
        return 403;
    }
}
