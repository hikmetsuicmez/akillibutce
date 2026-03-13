package com.akillibutce.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OcrYaniti(
        BigDecimal tutar,
        LocalDate tarih,
        double kdvOrani,
        String aciklama
) {}
