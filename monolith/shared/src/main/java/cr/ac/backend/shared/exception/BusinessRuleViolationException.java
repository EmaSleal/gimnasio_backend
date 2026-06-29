package cr.ac.backend.shared.exception;

public class BusinessRuleViolationException extends GymException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, String field) {
        super(message, field);
    }

    @Override
    public int getHttpStatus() {
        return 422;
    }
}
