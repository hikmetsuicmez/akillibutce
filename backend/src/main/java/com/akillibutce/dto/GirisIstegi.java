package com.akillibutce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GirisIstegi {

    @Email(message = "Gecerli bir e-posta adresi giriniz")
    @NotBlank(message = "E-posta bos birakilamaz")
    private String eposta;

    @NotBlank(message = "Sifre bos birakilamaz")
    private String sifre;
}
