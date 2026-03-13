package com.akillibutce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BirikimHedefiIstegi(
        @NotBlank String baslik,
        @NotNull @DecimalMin("0.01") BigDecimal hedefTutar,
        LocalDate sonTarih
) {}
