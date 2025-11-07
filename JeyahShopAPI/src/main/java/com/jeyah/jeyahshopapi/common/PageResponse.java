package com.jeyah.jeyahshopapi.common;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    // Mapper method
    public static <T, R> PageResponse<R> from(Page<T> pageData, Function<T, R> mapper) {
        List<R> mappedContent = pageData.getContent().stream()
                .map(mapper)
                .toList();

        // Use builder to avoid constructor mismatch
        return PageResponse.<R>builder()
                .content(mappedContent)
                .number(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .first(pageData.isFirst())
                .last(pageData.isLast())
                .build();
    }
}
