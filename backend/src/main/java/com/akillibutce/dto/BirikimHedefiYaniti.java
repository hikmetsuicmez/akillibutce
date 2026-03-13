package com.akillibutce.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BirikimHedefiYaniti(
        Long id,
        String baslik,
        BigDecimal hedefTutar,
        BigDecimal mevcutTutar,
        double ilerlemeYuzdesi,
        LocalDate sonTarih,
        String durum
) {}
