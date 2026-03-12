package com.akillibutce.controller;

import com.akillibutce.dto.TavsiyeYaniti;
import com.akillibutce.dto.TrendYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.KullaniciService;
import com.akillibutce.service.TavsiyeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analiz")
@RequiredArgsConstructor
public class AnalizController {

    private final TavsiyeService tavsiyeService;
    private final KullaniciService kullaniciService;

    // Premium endpoint - SecurityConfig'de koruma altinda
    @GetMapping("/tavsiyeler")
    public ResponseEntity<List<TavsiyeYaniti>> tavsiyeGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        List<TavsiyeYaniti> tavsiyeler = tavsiyeService.tavsiyeUret(kullanici);
        return ResponseEntity.ok(tavsiyeler);
    }

    // Premium endpoint - SecurityConfig'de koruma altinda
    @GetMapping("/trendler")
    public ResponseEntity<TrendYaniti> trendGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        TrendYaniti trend = tavsiyeService.altiAylikTrendGetir(kullanici);
        return ResponseEntity.ok(trend);
    }
}
