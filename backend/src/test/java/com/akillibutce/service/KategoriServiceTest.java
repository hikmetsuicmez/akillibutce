package com.akillibutce.service;

import com.akillibutce.config.PremiumTestKonfigurasyonu;
import com.akillibutce.dto.KategoriIstegi;
import com.akillibutce.dto.KategoriYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.entity.KategoriTip;
import com.akillibutce.repository.KullaniciRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kategori servisi testleri (premium profil ile calisir).
 */
@SpringBootTest
@ActiveProfiles("test-premium")
@Import(PremiumTestKonfigurasyonu.class)
@DisplayName("Kategori Service Testleri")
class KategoriServiceTest {

    @Autowired
    private KategoriService kategoriService;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    private Kullanici premiumKullanici() {
        return kullaniciRepository.findByEposta("premium@test.com").orElseThrow();
    }

    @Test
    @DisplayName("Premium kullanici sistem + ozel kategorilerini gorebilmeli")
    void premiumKullaniciTumKategorileriniGorebilmeli() {
        Kullanici kullanici = premiumKullanici();
        List<KategoriYaniti> kategoriler = kategoriService.kullanicininKategorileriGetir(kullanici);

        assertThat(kategoriler).isNotEmpty();
        // Sistem kategorileri de olmali
        assertThat(kategoriler).anyMatch(KategoriYaniti::isSistemKategorisi);
        // Ozel kategori de olmali (TestConfig'de "Tatil Fonu" eklendi)
        assertThat(kategoriler).anyMatch(k ->
                !k.isSistemKategorisi() && k.getIsim().equals("Tatil Fonu"));
    }

    @Test
    @DisplayName("Premium kullanici yeni ozel kategori ekleyebilmeli")
    void premiumKullaniciOzelKategoriEkleyebilmeli() {
        Kullanici kullanici = premiumKullanici();

        KategoriIstegi istek = new KategoriIstegi();
        istek.setIsim("Test Kategorisi");
        istek.setTip(KategoriTip.GIDER);
        istek.setZorunluMu(false);

        KategoriYaniti yanit = kategoriService.ozelKategoriEkle(istek, kullanici);

        assertThat(yanit.getId()).isNotNull();
        assertThat(yanit.getIsim()).isEqualTo("Test Kategorisi");
        assertThat(yanit.isSistemKategorisi()).isFalse();
    }

    @Test
    @DisplayName("Sistem kategorileri silinemez")
    void sistemKategorisiSilinemez() {
        Kullanici kullanici = premiumKullanici();
        Long sistemKategoriId = kategoriService.sistemKategorileriGetir()
                .stream().findFirst().orElseThrow().getId();

        assertThatThrownBy(() -> kategoriService.kategoriSil(sistemKategoriId, kullanici))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sistem kategorileri silinemez");
    }

    @Test
    @DisplayName("Sistem kategorileri listesi bos olmamali")
    void sistemKategorileriListesiBosOlmamali() {
        List<KategoriYaniti> sistemKategorileri = kategoriService.sistemKategorileriGetir();
        assertThat(sistemKategorileri).isNotEmpty();
        assertThat(sistemKategorileri).allMatch(KategoriYaniti::isSistemKategorisi);
    }
}
