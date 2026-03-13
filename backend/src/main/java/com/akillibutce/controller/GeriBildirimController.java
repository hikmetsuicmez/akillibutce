package com.akillibutce.controller;

import com.akillibutce.dto.GeriBildirimIstegi;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.GeriBildirimService;
import com.akillibutce.service.KullaniciService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geribildirim")
@RequiredArgsConstructor
public class GeriBildirimController {

    private final GeriBildirimService geriBildirimService;
    private final KullaniciService kullaniciService;

    @PostMapping
    public ResponseEntity<Void> geriBildirimGonder(
            @Valid @RequestBody GeriBildirimIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        geriBildirimService.geriBildirimGonder(istek, kullanici);
        return ResponseEntity.noContent().build();
    }
}
