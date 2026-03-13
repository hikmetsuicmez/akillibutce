package com.akillibutce.dto;

import java.time.LocalDateTime;

public record BildirimYaniti(
        Long id,
        String mesaj,
        boolean okunduMu,
        LocalDateTime olusturulmaTarihi
) {}
