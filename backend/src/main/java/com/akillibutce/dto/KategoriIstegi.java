package com.akillibutce.dto;

import com.akillibutce.entity.KategoriTip;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KategoriIstegi {

    @NotBlank(message = "Kategori ismi bos birakilamaz")
    private String isim;

    @NotNull(message = "Kategori tipi bos birakilamaz")
    private KategoriTip tip;

    private Boolean zorunluMu = false;
}
