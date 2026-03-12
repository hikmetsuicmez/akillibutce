package com.akillibutce.controller;

import com.akillibutce.dto.KategoriIstegi;
import com.akillibutce.dto.KategoriYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.KategoriService;
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
@RequestMapping("/api/kategoriler")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;
    private final KullaniciService kullaniciService;

    // Hem free hem premium kullanicilarin erisebilecegi endpoint
    @GetMapping("/liste")
    public ResponseEntity<List<KategoriYaniti>> kategorileriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        return ResponseEntity.ok(kategoriService.kullanicininKategorileriGetir(kullanici));
    }

    // Herkese acik - sistem kategorileri
    @GetMapping("/sistem")
    public ResponseEntity<List<KategoriYaniti>> sistemKategorileriGetir() {
        return ResponseEntity.ok(kategoriService.sistemKategorileriGetir());
    }

    // Sadece premium kullanicilara (SecurityConfig'de belirlendi)
    @PostMapping
    public ResponseEntity<KategoriYaniti> ozelKategoriEkle(
            @Valid @RequestBody KategoriIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        KategoriYaniti yanit = kategoriService.ozelKategoriEkle(istek, kullanici);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> kategoriSil(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        kategoriService.kategoriSil(id, kullanici);
        return ResponseEntity.noContent().build();
    }
}
