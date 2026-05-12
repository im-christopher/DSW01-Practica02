package com.dsw01.practica02.common.api;

public record ApiErrorResponse(
    String code,
    String message
) {
}
