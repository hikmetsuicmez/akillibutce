package com.akillibutce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record KategoriBurceLimitiIstegi(
        @NotNull Long kategoriId,
        @Min(1) @Max(12) int ay,
        @Min(2020) int yil,
        @NotNull @DecimalMin("0.01") BigDecimal limitTutar
) {}
