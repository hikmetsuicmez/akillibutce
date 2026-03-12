package com.akillibutce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KayitIstegi {

    @NotBlank(message = "Ad bos birakilamaz")
    private String ad;

    @NotBlank(message = "Soyad bos birakilamaz")
    private String soyad;

    @Email(message = "Gecerli bir e-posta adresi giriniz")
    @NotBlank(message = "E-posta bos birakilamaz")
    private String eposta;

    @NotBlank(message = "Sifre bos birakilamaz")
    @Size(min = 6, message = "Sifre en az 6 karakter olmalidir")
    private String sifre;
}
