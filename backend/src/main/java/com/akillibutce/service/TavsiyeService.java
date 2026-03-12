package com.akillibutce.service;

import com.akillibutce.dto.TavsiyeYaniti;
import com.akillibutce.dto.TrendYaniti;
import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.KategoriTip;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.IslemRepository;
import com.akillibutce.repository.KategoriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TavsiyeService {

    private final IslemRepository islemRepository;
    private final KategoriRepository kategoriRepository;

    @Value("${app.tavsiye.harcama-esik-yuzdesi:15}")
    private double harcamaEsikYuzdesi;

    @Transactional(readOnly = true)
    public List<TavsiyeYaniti> tavsiyeUret(Kullanici kullanici) {
        LocalDate bugun = LocalDate.now();
        int yil = bugun.getYear();
        int ay = bugun.getMonthValue();

        BigDecimal toplamGelir = islemRepository.findAylikToplamGelir(kullanici.getId(), yil, ay);

        if (toplamGelir.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        // Istege bagli (zorunlu olmayan) gider kategorilerini getir
        List<Kategori> istegeBagliKategoriler = kategoriRepository
                .findByTipAndKullanici(KategoriTip.GIDER, kullanici.getId())
                .stream()
                .filter(k -> !k.getZorunluMu())
                .toList();

        List<TavsiyeYaniti> tavsiyeler = new ArrayList<>();

        for (Kategori kategori : istegeBagliKategoriler) {
            BigDecimal kategoriHarcama = islemRepository.findKategoriAylikHarcama(
                    kullanici.getId(), kategori.getId(), yil, ay);

            if (kategoriHarcama.compareTo(BigDecimal.ZERO) == 0) continue;

            double harcamaYuzdesi = kategoriHarcama
                    .multiply(BigDecimal.valueOf(100))
                    .divide(toplamGelir, 2, RoundingMode.HALF_UP)
                    .doubleValue();

            if (harcamaYuzdesi > harcamaEsikYuzdesi) {
                String oncelik = oncelikBelirle(harcamaYuzdesi);
                String mesaj = mesajUret(kategori.getIsim(), harcamaYuzdesi, harcamaEsikYuzdesi);

                tavsiyeler.add(TavsiyeYaniti.builder()
                        .kategoriIsim(kategori.getIsim())
                        .harcamaMiktari(kategoriHarcama)
                        .toplamGelir(toplamGelir)
                        .harcamaYuzdesi(harcamaYuzdesi)
                        .esikYuzdesi(harcamaEsikYuzdesi)
                        .mesaj(mesaj)
                        .oncelik(oncelik)
                        .build());
            }
        }

        // Genel tasarruf tavsiyesi
        BigDecimal toplamGider = islemRepository.findAylikToplamGider(kullanici.getId(), yil, ay);
        double giderOrani = toplamGider
                .multiply(BigDecimal.valueOf(100))
                .divide(toplamGelir, 2, RoundingMode.HALF_UP)
                .doubleValue();

        if (giderOrani > 80) {
            tavsiyeler.add(0, TavsiyeYaniti.builder()
                    .kategoriIsim("Genel Harcama")
                    .harcamaMiktari(toplamGider)
                    .toplamGelir(toplamGelir)
                    .harcamaYuzdesi(giderOrani)
                    .esikYuzdesi(80)
                    .mesaj(String.format(
                            "Dikkat: Bu ay toplam harcamalariniz gelirinizin %%%.1f'ine ulasti. " +
                            "Tasarruf hedefinize ulasmak icin harcamalarinizi gozden gecirin.",
                            giderOrani))
                    .oncelik("YUKSEK")
                    .build());
        }

        tavsiyeler.sort(Comparator.comparing(TavsiyeYaniti::getHarcamaYuzdesi).reversed());
        return tavsiyeler;
    }

    @Transactional(readOnly = true)
    public TrendYaniti altiAylikTrendGetir(Kullanici kullanici) {
        LocalDate altiAyOnce = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        List<Object[]> veriler = islemRepository.findAltiAylikTrend(kullanici.getId(), altiAyOnce);

        // Son 6 ay icin harita olustur
        Map<String, BigDecimal[]> ayHarita = new LinkedHashMap<>();
        LocalDate dongu = altiAyOnce;
        for (int i = 0; i < 6; i++) {
            String ayAnahtari = dongu.getYear() + "-" + dongu.getMonthValue();
            ayHarita.put(ayAnahtari, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            dongu = dongu.plusMonths(1);
        }

        for (Object[] veri : veriler) {
            int yil = ((Number) veri[0]).intValue();
            int ay = ((Number) veri[1]).intValue();
            String tip = veri[2].toString();
            BigDecimal miktar = (BigDecimal) veri[3];
            String anahtar = yil + "-" + ay;

            if (ayHarita.containsKey(anahtar)) {
                BigDecimal[] degerler = ayHarita.get(anahtar);
                if ("GELIR".equals(tip)) {
                    degerler[0] = miktar;
                } else {
                    degerler[1] = miktar;
                }
            }
        }

        List<String> aylar = new ArrayList<>();
        List<BigDecimal> gelirler = new ArrayList<>();
        List<BigDecimal> giderler = new ArrayList<>();
        List<BigDecimal> netTasarruflar = new ArrayList<>();

        Locale trLocale = new Locale("tr", "TR");
        dongu = altiAyOnce;
        for (Map.Entry<String, BigDecimal[]> giris : ayHarita.entrySet()) {
            String ayIsmi = Month.of(dongu.getMonthValue())
                    .getDisplayName(TextStyle.SHORT, trLocale)
                    + " " + dongu.getYear();
            aylar.add(ayIsmi);

            BigDecimal gelir = giris.getValue()[0];
            BigDecimal gider = giris.getValue()[1];
            gelirler.add(gelir);
            giderler.add(gider);
            netTasarruflar.add(gelir.subtract(gider));
            dongu = dongu.plusMonths(1);
        }

        return TrendYaniti.builder()
                .aylar(aylar)
                .gelirler(gelirler)
                .giderler(giderler)
                .netTasarruflar(netTasarruflar)
                .build();
    }

    private String oncelikBelirle(double yuzde) {
        if (yuzde > 30) return "YUKSEK";
        if (yuzde > 20) return "ORTA";
        return "DUSUK";
    }

    private String mesajUret(String kategoriIsim, double harcamaYuzdesi, double esikYuzdesi) {
        return String.format(
                "Dikkat: %s harcamalariniz bu ay gelirinizin %%%.1f'ine ulasti " +
                "(esik: %%%.0f). Tasarruf etmek icin bu kalemi azaltmayi dusunebilirsiniz.",
                kategoriIsim, harcamaYuzdesi, esikYuzdesi);
    }
}
