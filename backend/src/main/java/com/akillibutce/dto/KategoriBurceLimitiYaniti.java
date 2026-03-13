package com.akillibutce.dto;

import java.math.BigDecimal;

public record KategoriBurceLimitiYaniti(
        Long id,
        Long kategoriId,
        String kategoriIsim,
        int ay,
        int yil,
        BigDecimal limitTutar,
        BigDecimal mevcutHarcama,
        double kullanımYuzdesi
) {}
