package com.akillibutce.controller;

import com.akillibutce.dto.GirisIstegi;
import com.akillibutce.dto.JwtYaniti;
import com.akillibutce.dto.KayitIstegi;
import com.akillibutce.service.KullaniciService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KullaniciService kullaniciService;

    @PostMapping("/kayit")
    public ResponseEntity<JwtYaniti> kayitOl(@Valid @RequestBody KayitIstegi istek) {
        JwtYaniti yanit = kullaniciService.kayitOl(istek);
        return ResponseEntity.status(HttpStatus.CREATED).body(yanit);
    }

    @PostMapping("/giris")
    public ResponseEntity<JwtYaniti> girisYap(@Valid @RequestBody GirisIstegi istek) {
        JwtYaniti yanit = kullaniciService.girisYap(istek);
        return ResponseEntity.ok(yanit);
    }
}
