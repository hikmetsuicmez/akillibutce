package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TavsiyeYaniti {

    private String kategoriIsim;
    private BigDecimal harcamaMiktari;
    private BigDecimal toplamGelir;
    private double harcamaYuzdesi;
    private double esikYuzdesi;
    private String mesaj;
    private String oncelik; // YUKSEK, ORTA, DUSUK
}
