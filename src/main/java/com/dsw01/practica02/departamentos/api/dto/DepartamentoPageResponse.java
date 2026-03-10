package com.dsw01.practica02.departamentos.api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record DepartamentoPageResponse(
    List<DepartamentoResponse> content,
    int number,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static DepartamentoPageResponse from(Page<DepartamentoResponse> page) {
        return new DepartamentoPageResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
