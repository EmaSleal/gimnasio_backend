package cr.ac.backend.shared.exception;

public class UnauthorizedException extends GymException {

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatus() {
        return 401;
    }
}
