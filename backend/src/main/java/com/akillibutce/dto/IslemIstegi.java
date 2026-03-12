package com.akillibutce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IslemIstegi {

    @NotNull(message = "Kategori ID bos birakilamaz")
    private Long kategoriId;

    @NotNull(message = "Miktar bos birakilamaz")
    @DecimalMin(value = "0.01", message = "Miktar 0'dan buyuk olmalidir")
    private BigDecimal miktar;

    private String aciklama;

    @NotNull(message = "Islem tarihi bos birakilamaz")
    private LocalDate islemTarihi;
}
