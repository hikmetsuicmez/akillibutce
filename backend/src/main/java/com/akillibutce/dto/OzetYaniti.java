package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OzetYaniti {

    private int yil;
    private int ay;
    private BigDecimal toplamGelir;
    private BigDecimal toplamGider;
    private BigDecimal netBakiye;
    private Map<String, BigDecimal> kategoriHarcamalari;
}
