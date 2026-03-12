package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendYaniti {

    private List<String> aylar;
    private List<BigDecimal> gelirler;
    private List<BigDecimal> giderler;
    private List<BigDecimal> netTasarruflar;
}
