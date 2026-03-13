package com.akillibutce.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DuzenliIslemYaniti(
        Long id,
        String kategoriIsim,
        String kategoriTip,
        BigDecimal tutar,
        String aciklama,
        String periyot,
        LocalDate gelecekIslemTarihi
) {}
