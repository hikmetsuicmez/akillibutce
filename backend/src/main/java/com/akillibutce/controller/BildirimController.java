package com.akillibutce.controller;

import com.akillibutce.dto.BildirimYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.BildirimService;
import com.akillibutce.service.KullaniciService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bildirimler")
@RequiredArgsConstructor
public class BildirimController {

    private final BildirimService bildirimService;
    private final KullaniciService kullaniciService;

    @GetMapping
    public ResponseEntity<List<BildirimYaniti>> bildirimleriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        return ResponseEntity.ok(bildirimService.bildirimleriGetir(kullanici));
    }

    @GetMapping("/sayac")
    public ResponseEntity<Map<String, Long>> okunmamisSayisi(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        long sayac = bildirimService.okunmamisSayisi(kullanici);
        return ResponseEntity.ok(Map.of("okunmamis", sayac));
    }

    @PostMapping("/tumu-okundu")
    public ResponseEntity<Void> tumunuOkunduIsaretle(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        bildirimService.tumunuOkunduIsaretle(kullanici);
        return ResponseEntity.noContent().build();
    }
}
