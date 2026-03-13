package com.akillibutce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GeriBildirimIstegi(
        @NotNull @Min(1) @Max(5) Integer puan,
        String mesaj
) {}
