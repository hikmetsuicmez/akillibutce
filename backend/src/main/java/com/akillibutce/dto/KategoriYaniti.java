package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KategoriYaniti {

    private Long id;
    private String isim;
    private String tip;
    private Boolean zorunluMu;
    private boolean sistemKategorisi;
}
