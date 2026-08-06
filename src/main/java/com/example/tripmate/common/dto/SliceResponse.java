package com.example.tripmate.common.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {

    private final List<T> content;
    private final int size;
    private final int number;
    private final boolean hasNext;

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getSize(),
                slice.getNumber(),
                slice.hasNext()
        );
    }
}
