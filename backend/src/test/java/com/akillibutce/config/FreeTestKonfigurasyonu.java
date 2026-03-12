package com.akillibutce.config;

import com.akillibutce.entity.*;
import com.akillibutce.repository.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * test-free profili aktifken calisir.
 * Ucretsiz kullanici senaryolarini test etmek icin
 * minimal mock veri olusturur.
 */
@TestConfiguration
@Profile("test-free")
public class FreeTestKonfigurasyonu {

    @Bean
    CommandLineRunner freeTestVerisiYukle(
            KullaniciRepository kullaniciRepository,
            KategoriRepository kategoriRepository,
            IslemRepository islemRepository,
            PasswordEncoder sifreKodlayici) {

        return args -> {
            // --- Ucretsiz Test Kullanicisi ---
            Kullanici freeKullanici = Kullanici.builder()
                    .ad("Ali")
                    .soyad("Free")
                    .eposta("free@test.com")
                    .sifre(sifreKodlayici.encode("Test1234!"))
                    .rol(KullaniciRol.ROLE_FREE_USER)
                    .build();
            freeKullanici = kullaniciRepository.save(freeKullanici);

            // --- Sistem Kategorileri ---
            Kategori maas = Kategori.builder()
                    .isim("Maaş").tip(KategoriTip.GELIR).zorunluMu(false).build();
            Kategori kira = Kategori.builder()
                    .isim("Kira").tip(KategoriTip.GIDER).zorunluMu(true).build();
            Kategori market = Kategori.builder()
                    .isim("Market").tip(KategoriTip.GIDER).zorunluMu(true).build();
            Kategori fatura = Kategori.builder()
                    .isim("Fatura").tip(KategoriTip.GIDER).zorunluMu(true).build();

            maas   = kategoriRepository.save(maas);
            kira   = kategoriRepository.save(kira);
            market = kategoriRepository.save(market);
            fatura = kategoriRepository.save(fatura);

            // --- Bu ayin temel islemleri ---
            LocalDate buAyBaslangic = LocalDate.now().withDayOfMonth(1);

            islemRepository.save(Islem.builder()
                    .kullanici(freeKullanici).kategori(maas)
                    .miktar(new BigDecimal("7500.00"))
                    .aciklama("Aylik maas")
                    .islemTarihi(buAyBaslangic).build());

            islemRepository.save(Islem.builder()
                    .kullanici(freeKullanici).kategori(kira)
                    .miktar(new BigDecimal("2500.00"))
                    .aciklama("Kira")
                    .islemTarihi(buAyBaslangic.plusDays(1)).build());

            islemRepository.save(Islem.builder()
                    .kullanici(freeKullanici).kategori(market)
                    .miktar(new BigDecimal("1200.00"))
                    .aciklama("Market alisverisi")
                    .islemTarihi(buAyBaslangic.plusDays(4)).build());

            islemRepository.save(Islem.builder()
                    .kullanici(freeKullanici).kategori(fatura)
                    .miktar(new BigDecimal("450.00"))
                    .aciklama("Elektrik, su, dogalgaz")
                    .islemTarihi(buAyBaslangic.plusDays(7)).build());
        };
    }
}
