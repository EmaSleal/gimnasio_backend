package cr.ac.backend.shared.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .total(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
