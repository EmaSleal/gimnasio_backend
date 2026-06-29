package cr.ac.backend.shared.exception.handler;

import cr.ac.backend.shared.exception.GymException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GymException.class)
    public ProblemDetail handleGymException(GymException ex) {
        var problem = ProblemDetail.forStatus(ex.getHttpStatus());
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/errors/" + slugFor(ex)));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Validation failed");
        problem.setType(URI.create("/errors/validation"));

        var errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    var field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
                    return Map.of("field", field, "message", error.getDefaultMessage());
                })
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Constraint violation");
        problem.setType(URI.create("/errors/validation"));

        var errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "field", v.getPropertyPath().toString(),
                        "message", v.getMessage()
                ))
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/errors/unauthorized"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setDetail("An unexpected error occurred");
        problem.setType(URI.create("/errors/internal"));
        return problem;
    }

    private String slugFor(GymException ex) {
        return switch (ex.getHttpStatus()) {
            case 404 -> "not-found";
            case 409 -> "conflict";
            case 401 -> "unauthorized";
            case 403 -> "forbidden";
            case 422 -> "business-rule";
            default -> "error";
        };
    }
}
