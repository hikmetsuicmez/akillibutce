package com.akillibutce.controller;

import com.akillibutce.dto.KategoriBurceLimitiIstegi;
import com.akillibutce.dto.KategoriBurceLimitiYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.KategoriBurceLimitiService;
import com.akillibutce.service.KullaniciService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/butce-limitleri")
@RequiredArgsConstructor
public class KategoriBurceLimitiController {

    private final KategoriBurceLimitiService limitService;
    private final KullaniciService kullaniciService;

    @PostMapping
    public ResponseEntity<KategoriBurceLimitiYaniti> limitKaydet(
            @Valid @RequestBody KategoriBurceLimitiIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        KategoriBurceLimitiYaniti yanit = limitService.limitKaydet(istek, kullanici);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @GetMapping
    public ResponseEntity<List<KategoriBurceLimitiYaniti>> limitleriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari,
            @RequestParam(defaultValue = "0") int ay,
            @RequestParam(defaultValue = "0") int yil) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        LocalDate bugun = LocalDate.now();
        int hedefAy = (ay == 0) ? bugun.getMonthValue() : ay;
        int hedefYil = (yil == 0) ? bugun.getYear() : yil;
        return ResponseEntity.ok(limitService.limitleriGetir(kullanici, hedefAy, hedefYil));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> limitSil(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        limitService.limitSil(id, kullanici);
        return ResponseEntity.noContent().build();
    }
}
