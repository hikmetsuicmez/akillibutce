package com.akillibutce.controller;

import com.akillibutce.dto.DuzenliIslemIstegi;
import com.akillibutce.dto.DuzenliIslemYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.service.DuzenliIslemService;
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
@RequestMapping("/api/duzenli-islemler")
@RequiredArgsConstructor
public class DuzenliIslemController {

    private final DuzenliIslemService duzenliIslemService;
    private final KullaniciService kullaniciService;

    @PostMapping
    public ResponseEntity<DuzenliIslemYaniti> duzenliIslemEkle(
            @Valid @RequestBody DuzenliIslemIstegi istek,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        DuzenliIslemYaniti yanit = duzenliIslemService.duzenliIslemEkle(istek, kullanici);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @GetMapping
    public ResponseEntity<List<DuzenliIslemYaniti>> duzenliIslemleriGetir(
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        return ResponseEntity.ok(duzenliIslemService.duzenliIslemleriGetir(kullanici));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> duzenliIslemSil(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails kullaniciDetaylari) {
        Kullanici kullanici = kullaniciService.epostayaGoreGetir(kullaniciDetaylari.getUsername());
        duzenliIslemService.duzenliIslemSil(id, kullanici);
        return ResponseEntity.noContent().build();
    }
}
