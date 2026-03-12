package com.akillibutce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtYaniti {

    private String token;
    private String tip = "Bearer";
    private Long id;
    private String ad;
    private String soyad;
    private String eposta;
    private String rol;
    private boolean premium;
}
