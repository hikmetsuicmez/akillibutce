package com.akillibutce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DuzenliIslemIstegi(
        @NotNull Long kategoriId,
        @NotNull @DecimalMin("0.01") BigDecimal tutar,
        @NotBlank String aciklama,
        @NotNull String periyot,
        @NotNull LocalDate gelecekIslemTarihi
) {}
