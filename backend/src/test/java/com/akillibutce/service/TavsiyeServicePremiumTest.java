package com.akillibutce.service;

import com.akillibutce.config.PremiumTestKonfigurasyonu;
import com.akillibutce.dto.TavsiyeYaniti;
import com.akillibutce.dto.TrendYaniti;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.KullaniciRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Premium kullanici senaryosu entegrasyon testleri.
 * Calistirmak icin: bash test-with-user.sh PREMIUM
 * veya: mvn test -Dspring.profiles.active=test-premium
 */
@SpringBootTest
@ActiveProfiles("test-premium")
@Import(PremiumTestKonfigurasyonu.class)
@DisplayName("Premium Kullanici - Tavsiye Motoru Testleri")
class TavsiyeServicePremiumTest {

    @Autowired
    private TavsiyeService tavsiyeService;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    private Kullanici premiumKullanici() {
        return kullaniciRepository.findByEposta("premium@test.com")
                .orElseThrow(() -> new IllegalStateException("Test verisi yuklenemedi"));
    }

    @Test
    @DisplayName("Esigi asan harcamalar icin tavsiye uretilmeli")
    void esigAsanHarcamalarIcinTavsiyeUretilmeli() {
        Kullanici kullanici = premiumKullanici();

        List<TavsiyeYaniti> tavsiyeler = tavsiyeService.tavsiyeUret(kullanici);

        // Disarida Yemek (%20) ve Eglenme (%18) esigi asti,
        // ayrica genel harcama uyarisi da bekleniyor
        assertThat(tavsiyeler).isNotEmpty();
        assertThat(tavsiyeler).anyMatch(t ->
                t.getKategoriIsim().equals("Dışarıda Yemek"));
        assertThat(tavsiyeler).anyMatch(t ->
                t.getKategoriIsim().equals("Eğlence"));
    }

    @Test
    @DisplayName("Her tavsiyenin mesaj alani dolu olmali")
    void tavsiyeMesajlariDoluOlmali() {
        List<TavsiyeYaniti> tavsiyeler = tavsiyeService.tavsiyeUret(premiumKullanici());

        assertThat(tavsiyeler).allSatisfy(t -> {
            assertThat(t.getMesaj()).isNotBlank();
            assertThat(t.getKategoriIsim()).isNotBlank();
            assertThat(t.getOncelik()).isIn("YUKSEK", "ORTA", "DUSUK");
        });
    }

    @Test
    @DisplayName("Tavsiyeler harcama yuzdesi azalarak sirali olmali")
    void tavsiyelerYuzdeAzalarakSiraliOlmali() {
        List<TavsiyeYaniti> tavsiyeler = tavsiyeService.tavsiyeUret(premiumKullanici());

        for (int i = 0; i < tavsiyeler.size() - 1; i++) {
            assertThat(tavsiyeler.get(i).getHarcamaYuzdesi())
                    .isGreaterThanOrEqualTo(tavsiyeler.get(i + 1).getHarcamaYuzdesi());
        }
    }

    @Test
    @DisplayName("6 aylik trend verisi 6 ay icermeli")
    void altiAylikTrendAltiAyIcermeli() {
        TrendYaniti trend = tavsiyeService.altiAylikTrendGetir(premiumKullanici());

        assertThat(trend.getAylar()).hasSize(6);
        assertThat(trend.getGelirler()).hasSize(6);
        assertThat(trend.getGiderler()).hasSize(6);
        assertThat(trend.getNetTasarruflar()).hasSize(6);
    }

    @Test
    @DisplayName("Trend verisinde en az bir ayda gelir > 0 olmali")
    void trendVerisindeBirAydaGelirOlmali() {
        TrendYaniti trend = tavsiyeService.altiAylikTrendGetir(premiumKullanici());

        assertThat(trend.getGelirler())
                .anyMatch(g -> g.doubleValue() > 0);
    }

    @Test
    @DisplayName("Premium kullanici tavsiye motoruna erisebilmeli")
    void premiumKullaniciTavsiyeMotorunaErisebilmeli() {
        Kullanici kullanici = premiumKullanici();

        assertThat(kullanici.isPremium()).isTrue();
        // Exception firlatmamasi erisme yetkisi oldugunu gosterir
        assertThat(tavsiyeService.tavsiyeUret(kullanici)).isNotNull();
    }
}
