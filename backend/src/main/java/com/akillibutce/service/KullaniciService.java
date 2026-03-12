package com.akillibutce.service;

import com.akillibutce.dto.JwtYaniti;
import com.akillibutce.dto.GirisIstegi;
import com.akillibutce.dto.KayitIstegi;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.entity.KullaniciRol;
import com.akillibutce.repository.KullaniciRepository;
import com.akillibutce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KullaniciService {

    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder sifreKodlayici;
    private final AuthenticationManager kimlikDogrulamaYoneticisi;
    private final JwtUtil jwtUtil;

    @Transactional
    public JwtYaniti kayitOl(KayitIstegi istek) {
        if (kullaniciRepository.existsByEposta(istek.getEposta())) {
            throw new IllegalStateException("Bu e-posta adresi zaten kayitli: " + istek.getEposta());
        }

        Kullanici yeniKullanici = Kullanici.builder()
                .ad(istek.getAd())
                .soyad(istek.getSoyad())
                .eposta(istek.getEposta())
                .sifre(sifreKodlayici.encode(istek.getSifre()))
                .rol(KullaniciRol.ROLE_FREE_USER)
                .build();

        Kullanici kaydedilenKullanici = kullaniciRepository.save(yeniKullanici);

        Authentication auth = kimlikDogrulamaYoneticisi.authenticate(
                new UsernamePasswordAuthenticationToken(istek.getEposta(), istek.getSifre())
        );

        String token = jwtUtil.tokenUret((UserDetails) auth.getPrincipal());

        return JwtYaniti.builder()
                .token(token)
                .tip("Bearer")
                .id(kaydedilenKullanici.getId())
                .ad(kaydedilenKullanici.getAd())
                .soyad(kaydedilenKullanici.getSoyad())
                .eposta(kaydedilenKullanici.getEposta())
                .rol(kaydedilenKullanici.getRol().name())
                .premium(kaydedilenKullanici.isPremium())
                .build();
    }

    public JwtYaniti girisYap(GirisIstegi istek) {
        Authentication auth = kimlikDogrulamaYoneticisi.authenticate(
                new UsernamePasswordAuthenticationToken(istek.getEposta(), istek.getSifre())
        );

        String token = jwtUtil.tokenUret((UserDetails) auth.getPrincipal());

        Kullanici kullanici = kullaniciRepository.findByEposta(istek.getEposta())
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi"));

        return JwtYaniti.builder()
                .token(token)
                .tip("Bearer")
                .id(kullanici.getId())
                .ad(kullanici.getAd())
                .soyad(kullanici.getSoyad())
                .eposta(kullanici.getEposta())
                .rol(kullanici.getRol().name())
                .premium(kullanici.isPremium())
                .build();
    }

    @Transactional(readOnly = true)
    public Kullanici epostayaGoreGetir(String eposta) {
        return kullaniciRepository.findByEposta(eposta)
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi: " + eposta));
    }
}
