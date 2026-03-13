package com.akillibutce.service;

import com.akillibutce.dto.KategoriBurceLimitiIstegi;
import com.akillibutce.dto.KategoriBurceLimitiYaniti;
import com.akillibutce.entity.KategoriBurceLimiti;
import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.exception.ResourceNotFoundException;
import com.akillibutce.repository.IslemRepository;
import com.akillibutce.repository.KategoriBurceLimitiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KategoriBurceLimitiService {

    private final KategoriBurceLimitiRepository limitRepository;
    private final IslemRepository islemRepository;
    private final KategoriService kategoriService;
    private final BildirimService bildirimService;

    @Transactional
    public KategoriBurceLimitiYaniti limitKaydet(KategoriBurceLimitiIstegi istek, Kullanici kullanici) {
        if (!kullanici.isPremium()) {
            throw new IllegalStateException("Bütçe limiti belirleme Premium özelliğidir.");
        }

        Kategori kategori = kategoriService.idyeGoreGetir(istek.kategoriId());

        Optional<KategoriBurceLimiti> mevcutLimit = limitRepository
                .findByKullaniciIdAndKategoriIdAndAyAndYil(
                        kullanici.getId(), kategori.getId(), istek.ay(), istek.yil());

        KategoriBurceLimiti limit;
        if (mevcutLimit.isPresent()) {
            limit = mevcutLimit.get();
            limit.setLimitTutar(istek.limitTutar());
        } else {
            limit = KategoriBurceLimiti.builder()
                    .kullanici(kullanici)
                    .kategori(kategori)
                    .ay(istek.ay())
                    .yil(istek.yil())
                    .limitTutar(istek.limitTutar())
                    .build();
        }

        return entitydenYanitaDonustur(limitRepository.save(limit), kullanici);
    }

    @Transactional(readOnly = true)
    public List<KategoriBurceLimitiYaniti> limitleriGetir(Kullanici kullanici, int ay, int yil) {
        if (!kullanici.isPremium()) {
            throw new IllegalStateException("Bütçe limiti görüntüleme Premium özelliğidir.");
        }
        return limitRepository.findByKullaniciIdAndAyAndYil(kullanici.getId(), ay, yil)
                .stream()
                .map(l -> entitydenYanitaDonustur(l, kullanici))
                .collect(Collectors.toList());
    }

    @Transactional
    public void limitSil(Long limitId, Kullanici kullanici) {
        KategoriBurceLimiti limit = limitRepository.findById(limitId)
                .orElseThrow(() -> new ResourceNotFoundException("KategoriBurceLimiti", limitId));
        if (!limit.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu limite erişim yetkiniz yok.");
        }
        limitRepository.delete(limit);
    }

    /**
     * İşlem eklendikten sonra bütçe kontrolü yapar ve gerekirse bildirim oluşturur.
     */
    @Transactional
    public void butceKontrolYap(Kullanici kullanici, Long kategoriId, LocalDate islemTarihi) {
        if (!kullanici.isPremium()) return;

        int ay = islemTarihi.getMonthValue();
        int yil = islemTarihi.getYear();

        limitRepository.findByKullaniciIdAndKategoriIdAndAyAndYil(kullanici.getId(), kategoriId, ay, yil)
                .ifPresent(limit -> {
                    BigDecimal harcama = islemRepository.findKategoriAylikHarcama(
                            kullanici.getId(), kategoriId, yil, ay);

                    if (harcama == null) harcama = BigDecimal.ZERO;

                    double yuzde = harcama.multiply(BigDecimal.valueOf(100))
                            .divide(limit.getLimitTutar(), 2, RoundingMode.HALF_UP)
                            .doubleValue();

                    if (yuzde >= 100) {
                        bildirimService.bildirimOlustur(kullanici,
                                String.format("⚠️ %s kategorisinde aylık bütçe limitinizi (%s ₺) aştınız! Mevcut harcama: %s ₺",
                                        limit.getKategori().getIsim(),
                                        limit.getLimitTutar().toPlainString(),
                                        harcama.toPlainString()));
                    } else if (yuzde >= 80) {
                        bildirimService.bildirimOlustur(kullanici,
                                String.format("📊 %s kategorisinde aylık bütçe limitinizin %%%.0f'ine ulaştınız. Limit: %s ₺, Harcama: %s ₺",
                                        limit.getKategori().getIsim(), yuzde,
                                        limit.getLimitTutar().toPlainString(),
                                        harcama.toPlainString()));
                    }
                });
    }

    private KategoriBurceLimitiYaniti entitydenYanitaDonustur(KategoriBurceLimiti limit, Kullanici kullanici) {
        BigDecimal harcama = islemRepository.findKategoriAylikHarcama(
                kullanici.getId(), limit.getKategori().getId(), limit.getYil(), limit.getAy());
        if (harcama == null) harcama = BigDecimal.ZERO;

        double yuzde = 0.0;
        if (limit.getLimitTutar().compareTo(BigDecimal.ZERO) > 0) {
            yuzde = harcama.multiply(BigDecimal.valueOf(100))
                    .divide(limit.getLimitTutar(), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new KategoriBurceLimitiYaniti(
                limit.getId(),
                limit.getKategori().getId(),
                limit.getKategori().getIsim(),
                limit.getAy(),
                limit.getYil(),
                limit.getLimitTutar(),
                harcama,
                yuzde
        );
    }
}
