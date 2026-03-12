package com.akillibutce.service;

import com.akillibutce.config.FreeTestKonfigurasyonu;
import com.akillibutce.dto.IslemIstegi;
import com.akillibutce.dto.IslemYaniti;
import com.akillibutce.dto.OzetYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.entity.KullaniciRol;
import com.akillibutce.repository.KategoriRepository;
import com.akillibutce.repository.KullaniciRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ucretsiz kullanici senaryosu entegrasyon testleri.
 * Calistirmak icin: bash test-with-user.sh FREE
 * veya: mvn test -Dspring.profiles.active=test-free
 */
@SpringBootTest
@ActiveProfiles("test-free")
@Import(FreeTestKonfigurasyonu.class)
@DisplayName("Free Kullanici - Islem ve Ozet Testleri")
class IslemServiceFreeTest {

    @Autowired
    private IslemService islemService;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    private Kullanici freeKullanici() {
        return kullaniciRepository.findByEposta("free@test.com")
                .orElseThrow(() -> new IllegalStateException("Test verisi yuklenemedi"));
    }

    @Test
    @DisplayName("Free kullanici ROLE_FREE_USER rolune sahip olmali")
    void freeKullaniciDogruRoleSahip() {
        Kullanici kullanici = freeKullanici();
        assertThat(kullanici.getRol()).isEqualTo(KullaniciRol.ROLE_FREE_USER);
        assertThat(kullanici.isPremium()).isFalse();
    }

    @Test
    @DisplayName("Free kullanici islem ekleyebilmeli")
    void freeKullaniciIslemEkleyebilmeli() {
        Kullanici kullanici = freeKullanici();
        Long marketId = kategoriRepository.findByKullaniciIsNull()
                .stream()
                .filter(k -> k.getIsim().equals("Market"))
                .findFirst()
                .orElseThrow()
                .getId();

        IslemIstegi istek = new IslemIstegi();
        istek.setKategoriId(marketId);
        istek.setMiktar(new BigDecimal("250.00"));
        istek.setAciklama("Test market alişverişi");
        istek.setIslemTarihi(LocalDate.now());

        IslemYaniti yanit = islemService.islemEkle(istek, kullanici);

        assertThat(yanit.getId()).isNotNull();
        assertThat(yanit.getMiktar()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(yanit.getKategoriIsim()).isEqualTo("Market");
    }

    @Test
    @DisplayName("Free kullanici kendi islemlerini listeleyebilmeli")
    void freeKullaniciIslemleriniListeleyebilmeli() {
        Kullanici kullanici = freeKullanici();

        List<IslemYaniti> islemler = islemService.islemleriGetir(kullanici, null, null);

        // FreeTestKonfigurasyonu 4 islem ekledi
        assertThat(islemler).hasSize(4);
    }

    @Test
    @DisplayName("Aylik ozet dogru toplam gelir ve gider hesaplamali")
    void aylikOzetDogruHesaplamali() {
        Kullanici kullanici = freeKullanici();
        int yil = LocalDate.now().getYear();
        int ay = LocalDate.now().getMonthValue();

        OzetYaniti ozet = islemService.aylikOzetGetir(kullanici, yil, ay);

        // Gelir: 7500
        assertThat(ozet.getToplamGelir()).isEqualByComparingTo(new BigDecimal("7500.00"));
        // Gider: 2500 + 1200 + 450 = 4150
        assertThat(ozet.getToplamGider()).isEqualByComparingTo(new BigDecimal("4150.00"));
        // Net: 7500 - 4150 = 3350
        assertThat(ozet.getNetBakiye()).isEqualByComparingTo(new BigDecimal("3350.00"));
    }

    @Test
    @DisplayName("Islem silme sonrasi liste kisalmali")
    void islemSilindiktenSonraListeKisalmali() {
        Kullanici kullanici = freeKullanici();
        Long marketId = kategoriRepository.findByKullaniciIsNull()
                .stream()
                .filter(k -> k.getIsim().equals("Market"))
                .findFirst()
                .orElseThrow()
                .getId();

        // Yeni islem ekle
        IslemIstegi istek = new IslemIstegi();
        istek.setKategoriId(marketId);
        istek.setMiktar(new BigDecimal("100.00"));
        istek.setIslemTarihi(LocalDate.now());
        IslemYaniti yeniIslem = islemService.islemEkle(istek, kullanici);

        int oncekiSayi = islemService.islemleriGetir(kullanici, null, null).size();

        // Sil
        islemService.islemSil(yeniIslem.getId(), kullanici);

        int sonrakiSayi = islemService.islemleriGetir(kullanici, null, null).size();
        assertThat(sonrakiSayi).isEqualTo(oncekiSayi - 1);
    }

    @Test
    @DisplayName("Tarih filtreli sorguda sadece o tarihteki islemler gelmeli")
    void tarihFiltresiDogrudanIslemleriGetirmeli() {
        Kullanici kullanici = freeKullanici();
        LocalDate buAy = LocalDate.now().withDayOfMonth(1);

        List<IslemYaniti> islemler = islemService.islemleriGetir(
                kullanici, buAy, buAy.plusMonths(1).minusDays(1));

        assertThat(islemler).isNotEmpty();
        assertThat(islemler).allSatisfy(i -> {
            assertThat(i.getIslemTarihi()).isAfterOrEqualTo(buAy);
            assertThat(i.getIslemTarihi()).isBeforeOrEqualTo(buAy.plusMonths(1).minusDays(1));
        });
    }
}
