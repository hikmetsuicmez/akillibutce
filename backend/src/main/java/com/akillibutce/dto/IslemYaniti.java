package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IslemYaniti {

    private Long id;
    private Long kategoriId;
    private String kategoriIsim;
    private String kategoriTip;
    private BigDecimal miktar;
    private String aciklama;
    private LocalDate islemTarihi;
}
