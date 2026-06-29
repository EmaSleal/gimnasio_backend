package cr.ac.backend.shared.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiResponse<T> {

    private final T data;
    private final String message;
    private final Instant timestamp;

    public static <T> ApiResponse<T> of(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> of(T data) {
        return of(data, "OK");
    }
}
