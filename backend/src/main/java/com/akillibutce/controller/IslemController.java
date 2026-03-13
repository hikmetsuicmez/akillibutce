package com.akillibutce.controller;

import com.akillibutce.dto.IslemIstegi;
import com.akillibutce.dto.IslemYaniti;
import com.akillibutce.dto.OcrYaniti;
import com.akillibutce.dto.OzetYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.IslemService;
import com.akillibutce.service.KullaniciService;
import com.akillibutce.service.OcrMockServisi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/islemler")
@RequiredArgsConstructor
public class IslemController {

    private final IslemService islemService;
    private final KullaniciService kullaniciService;
    private final OcrMockServisi ocrMockServisi;

    @PostMapping
    public ResponseEntity<IslemYaniti> islemEkle(
            @Valid @RequestBody IslemIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        IslemYaniti yanit = islemService.islemEkle(istek, kullanici);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @GetMapping
    public ResponseEntity<List<IslemYaniti>> islemleriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangic,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitis) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        List<IslemYaniti> islemler = islemService.islemleriGetir(kullanici, baslangic, bitis);
        return ResponseEntity.ok(islemler);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> islemSil(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        islemService.islemSil(id, kullanici);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ozet")
    public ResponseEntity<OzetYaniti> aylikOzetGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari,
            @RequestParam(defaultValue = "0") int yil,
            @RequestParam(defaultValue = "0") int ay) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        LocalDate bugun = LocalDate.now();
        int hedefYil = (yil == 0) ? bugun.getYear() : yil;
        int hedefAy = (ay == 0) ? bugun.getMonthValue() : ay;
        OzetYaniti ozet = islemService.aylikOzetGetir(kullanici, hedefYil, hedefAy);
        return ResponseEntity.ok(ozet);
    }

    @PostMapping("/ocr-tara")
    public ResponseEntity<OcrYaniti> fisTara(
            @RequestParam("dosya") MultipartFile dosya,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        if (!kullanici.isPremium()) {
            return ResponseEntity.status(403).build();
        }
        OcrYaniti yanit = ocrMockServisi.fisOku(dosya);
        return ResponseEntity.ok(yanit);
    }
}
