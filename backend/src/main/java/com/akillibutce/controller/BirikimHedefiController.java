package com.akillibutce.controller;

import com.akillibutce.dto.BirikimHedefiIstegi;
import com.akillibutce.dto.BirikimHedefiYaniti;
import com.akillibutce.dto.ParaEkleIstegi;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.BirikimHedefiService;
import com.akillibutce.service.KullaniciService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hedefler")
@RequiredArgsConstructor
public class BirikimHedefiController {

    private final BirikimHedefiService birikimHedefiService;
    private final KullaniciService kullaniciService;

    @PostMapping
    public ResponseEntity<BirikimHedefiYaniti> hedefOlustur(
            @Valid @RequestBody BirikimHedefiIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        BirikimHedefiYaniti yanit = birikimHedefiService.hedefOlustur(istek, kullanici);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @GetMapping
    public ResponseEntity<List<BirikimHedefiYaniti>> hedefleriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        return ResponseEntity.ok(birikimHedefiService.hedefleriGetir(kullanici));
    }

    @PostMapping("/{id}/para-ekle")
    public ResponseEntity<BirikimHedefiYaniti> paraEkle(
            @PathVariable Long id,
            @Valid @RequestBody ParaEkleIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        BirikimHedefiYaniti yanit = birikimHedefiService.paraEkle(id, istek.miktar(), kullanici);
        return ResponseEntity.ok(yanit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hedefSil(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        birikimHedefiService.hedefSil(id, kullanici);
        return ResponseEntity.noContent().build();
    }
}
