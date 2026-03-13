package com.akillibutce.service;

import com.akillibutce.dto.DuzenliIslemIstegi;
import com.akillibutce.dto.DuzenliIslemYaniti;
import com.akillibutce.entity.DuzenliIslem;
import com.akillibutce.entity.Islem;
import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.entity.Periyot;
import com.akillibutce.exception.ResourceNotFoundException;
import com.akillibutce.repository.DuzenliIslemRepository;
import com.akillibutce.repository.IslemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuzenliIslemService {

    private final DuzenliIslemRepository duzenliIslemRepository;
    private final IslemRepository islemRepository;
    private final KategoriService kategoriService;

    @Transactional
    public DuzenliIslemYaniti duzenliIslemEkle(DuzenliIslemIstegi istek, Kullanici kullanici) {
        if (!kullanici.isPremium()) {
            throw new IllegalStateException("Duzenli islem ekleme Premium ozelligidir.");
        }

        Kategori kategori = kategoriService.idyeGoreGetir(istek.kategoriId());

        DuzenliIslem duzenliIslem = DuzenliIslem.builder()
                .kullanici(kullanici)
                .kategori(kategori)
                .tutar(istek.tutar())
                .aciklama(istek.aciklama())
                .periyot(Periyot.valueOf(istek.periyot()))
                .gelecekIslemTarihi(istek.gelecekIslemTarihi())
                .build();

        return entitydenYanitaDonustur(duzenliIslemRepository.save(duzenliIslem));
    }

    @Transactional(readOnly = true)
    public List<DuzenliIslemYaniti> duzenliIslemleriGetir(Kullanici kullanici) {
        if (!kullanici.isPremium()) {
            throw new IllegalStateException("Duzenli islemler Premium ozelligidir.");
        }
        return duzenliIslemRepository
                .findByKullaniciIdAndAktifTrueOrderByGelecekIslemTarihiAsc(kullanici.getId())
                .stream()
                .map(this::entitydenYanitaDonustur)
                .collect(Collectors.toList());
    }

    @Transactional
    public void duzenliIslemSil(Long islemId, Kullanici kullanici) {
        DuzenliIslem duzenliIslem = duzenliIslemRepository.findById(islemId)
                .orElseThrow(() -> new ResourceNotFoundException("DuzenliIslem", islemId));
        if (!duzenliIslem.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu isleme erisim yetkiniz yok.");
        }
        duzenliIslem.setAktif(false);
        duzenliIslemRepository.save(duzenliIslem);
    }

    /**
     * Her gün sabah 00:05'te çalışır.
     * Günü gelen düzenli işlemleri kullanıcının işlem listesine otomatik ekler.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void bekleyenDuzenliIslemleriisle() {
        LocalDate bugun = LocalDate.now();
        List<DuzenliIslem> bekleyenler = duzenliIslemRepository
                .findByAktifTrueAndGelecekIslemTarihiLessThanEqual(bugun);

        log.info("Islenecek duzenli islem sayisi: {}", bekleyenler.size());

        for (DuzenliIslem duzenliIslem : bekleyenler) {
            try {
                Islem yeniIslem = Islem.builder()
                        .kullanici(duzenliIslem.getKullanici())
                        .kategori(duzenliIslem.getKategori())
                        .miktar(duzenliIslem.getTutar())
                        .aciklama("[Otomatik] " + duzenliIslem.getAciklama())
                        .islemTarihi(bugun)
                        .build();
                islemRepository.save(yeniIslem);

                // Sonraki tarihi güncelle
                LocalDate sonrakiTarih = (duzenliIslem.getPeriyot() == Periyot.AYLIK)
                        ? duzenliIslem.getGelecekIslemTarihi().plusMonths(1)
                        : duzenliIslem.getGelecekIslemTarihi().plusYears(1);
                duzenliIslem.setGelecekIslemTarihi(sonrakiTarih);
                duzenliIslemRepository.save(duzenliIslem);

                log.info("Duzenli islem islendi: id={}, kullanici={}", duzenliIslem.getId(),
                        duzenliIslem.getKullanici().getEposta());
            } catch (Exception e) {
                log.error("Duzenli islem islenirken hata olustu: id={}", duzenliIslem.getId(), e);
            }
        }
    }

    private DuzenliIslemYaniti entitydenYanitaDonustur(DuzenliIslem duzenliIslem) {
        return new DuzenliIslemYaniti(
                duzenliIslem.getId(),
                duzenliIslem.getKategori().getIsim(),
                duzenliIslem.getKategori().getTip().name(),
                duzenliIslem.getTutar(),
                duzenliIslem.getAciklama(),
                duzenliIslem.getPeriyot().name(),
                duzenliIslem.getGelecekIslemTarihi()
        );
    }
}
