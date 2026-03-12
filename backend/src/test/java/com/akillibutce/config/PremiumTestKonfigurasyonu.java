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
 * test-premium profili aktifken calisir.
 * Veritabanina bir Premium kullanici ve ornek islemler ekler.
 * Tavsiye motoru testleri bu veriyle calisir.
 */
@TestConfiguration
@Profile("test-premium")
public class PremiumTestKonfigurasyonu {

    @Bean
    CommandLineRunner premiumTestVerisiYukle(
            KullaniciRepository kullaniciRepository,
            KategoriRepository kategoriRepository,
            IslemRepository islemRepository,
            AbonelikRepository abonelikRepository,
            PasswordEncoder sifreKodlayici) {

        return args -> {
            // --- Premium Test Kullanicisi ---
            Kullanici premiumKullanici = Kullanici.builder()
                    .ad("Ayse")
                    .soyad("Premium")
                    .eposta("premium@test.com")
                    .sifre(sifreKodlayici.encode("Test1234!"))
                    .rol(KullaniciRol.ROLE_PREMIUM_USER)
                    .build();
            premiumKullanici = kullaniciRepository.save(premiumKullanici);

            // --- Abonelik ---
            Abonelik abonelik = Abonelik.builder()
                    .kullanici(premiumKullanici)
                    .baslangicTarihi(LocalDate.now().minusMonths(1))
                    .bitisTarihi(LocalDate.now().plusMonths(11))
                    .durum(AbonelikDurum.AKTIF)
                    .tip(AbonelikTip.YILLIK)
                    .build();
            abonelikRepository.save(abonelik);

            // --- Sistem Kategorileri ---
            Kategori maas = Kategori.builder()
                    .isim("Maaş").tip(KategoriTip.GELIR).zorunluMu(false).build();
            Kategori kira = Kategori.builder()
                    .isim("Kira").tip(KategoriTip.GIDER).zorunluMu(true).build();
            Kategori disaridaYemek = Kategori.builder()
                    .isim("Dışarıda Yemek").tip(KategoriTip.GIDER).zorunluMu(false).build();
            Kategori eglenme = Kategori.builder()
                    .isim("Eğlence").tip(KategoriTip.GIDER).zorunluMu(false).build();
            Kategori market = Kategori.builder()
                    .isim("Market").tip(KategoriTip.GIDER).zorunluMu(true).build();

            maas          = kategoriRepository.save(maas);
            kira          = kategoriRepository.save(kira);
            disaridaYemek = kategoriRepository.save(disaridaYemek);
            eglenme       = kategoriRepository.save(eglenme);
            market        = kategoriRepository.save(market);

            // --- Premium Kullanicinin Ozel Kategorisi ---
            Kategori ozelKategori = Kategori.builder()
                    .isim("Tatil Fonu")
                    .tip(KategoriTip.GIDER)
                    .zorunluMu(false)
                    .kullanici(premiumKullanici)
                    .build();
            ozelKategori = kategoriRepository.save(ozelKategori);

            // --- Bu ayin islemleri (tavsiye motoru tetiklenecek sekilde) ---
            LocalDate buAyBaslangic = LocalDate.now().withDayOfMonth(1);

            // Gelir: 10.000 TL maas
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(maas)
                    .miktar(new BigDecimal("10000.00"))
                    .aciklama("Haziran maaşı")
                    .islemTarihi(buAyBaslangic).build());

            // Gider: Kira 3000 TL (%30 - zorunlu, tavsiye cikmamali)
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(kira)
                    .miktar(new BigDecimal("3000.00"))
                    .aciklama("Haziran kirası")
                    .islemTarihi(buAyBaslangic.plusDays(1)).build());

            // Gider: Disarida Yemek 2000 TL (%20 - esigi asti, tavsiye cikmali)
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(disaridaYemek)
                    .miktar(new BigDecimal("1200.00"))
                    .aciklama("Restoran")
                    .islemTarihi(buAyBaslangic.plusDays(5)).build());
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(disaridaYemek)
                    .miktar(new BigDecimal("800.00"))
                    .aciklama("Cafe ve takeaway")
                    .islemTarihi(buAyBaslangic.plusDays(12)).build());

            // Gider: Eglenme 1800 TL (%18 - esigi asti, tavsiye cikmali)
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(eglenme)
                    .miktar(new BigDecimal("1800.00"))
                    .aciklama("Konser, sinema")
                    .islemTarihi(buAyBaslangic.plusDays(8)).build());

            // Gider: Market 800 TL (makul, tavsiye cikmamali)
            islemRepository.save(Islem.builder()
                    .kullanici(premiumKullanici).kategori(market)
                    .miktar(new BigDecimal("800.00"))
                    .aciklama("Haftalık alışveriş")
                    .islemTarihi(buAyBaslangic.plusDays(3)).build());

            // --- 6 aylik trend icin gecmis veriler ---
            for (int ay = 5; ay >= 1; ay--) {
                LocalDate gecmisAy = LocalDate.now().minusMonths(ay).withDayOfMonth(1);
                BigDecimal gelir = new BigDecimal(9000 + (ay * 200));
                BigDecimal giderTutar = new BigDecimal(6000 + (ay * 100));

                islemRepository.save(Islem.builder()
                        .kullanici(premiumKullanici).kategori(maas)
                        .miktar(gelir)
                        .aciklama("Gecmis ay maasi")
                        .islemTarihi(gecmisAy).build());

                islemRepository.save(Islem.builder()
                        .kullanici(premiumKullanici).kategori(market)
                        .miktar(giderTutar)
                        .aciklama("Gecmis ay harcamasi")
                        .islemTarihi(gecmisAy.plusDays(5)).build());
            }
        };
    }
}
