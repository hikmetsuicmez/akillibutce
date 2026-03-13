package com.akillibutce.service;

import com.akillibutce.dto.IslemIstegi;
import com.akillibutce.dto.IslemYaniti;
import com.akillibutce.dto.OzetYaniti;
import com.akillibutce.entity.Islem;
import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.exception.ResourceNotFoundException;
import com.akillibutce.repository.IslemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IslemService {

    private final IslemRepository islemRepository;
    private final KategoriService kategoriService;
    private final KategoriBurceLimitiService kategoriBurceLimitiService;

    @Transactional
    public IslemYaniti islemEkle(IslemIstegi istek, Kullanici kullanici) {
        Kategori kategori = kategoriService.idyeGoreGetir(istek.getKategoriId());

        // Ozel kategorinin bu kullaniciya ait olup olmadigini kontrol et
        if (!kategori.isSistemKategorisi() &&
                !kategori.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu kategoriyi kullanamazsiniz.");
        }

        Islem yeniIslem = Islem.builder()
                .kullanici(kullanici)
                .kategori(kategori)
                .miktar(istek.getMiktar())
                .aciklama(istek.getAciklama())
                .islemTarihi(istek.getIslemTarihi())
                .build();

        Islem kaydedilen = islemRepository.save(yeniIslem);

        if ("GIDER".equals(kategori.getTip().name())) {
            kategoriBurceLimitiService.butceKontrolYap(
                    kullanici, kategori.getId(), istek.getIslemTarihi());
        }

        return entitydenYanitadonustur(kaydedilen);
    }

    @Transactional(readOnly = true)
    public List<IslemYaniti> islemleriGetir(Kullanici kullanici,
                                             LocalDate baslangic,
                                             LocalDate bitis) {
        List<Islem> islemler;
        if (baslangic != null && bitis != null) {
            islemler = islemRepository
                    .findByKullaniciIdAndIslemTarihiBetweenOrderByIslemTarihiDesc(
                            kullanici.getId(), baslangic, bitis);
        } else {
            islemler = islemRepository
                    .findByKullaniciIdOrderByIslemTarihiDesc(kullanici.getId());
        }
        return islemler.stream()
                .map(this::entitydenYanitadonustur)
                .collect(Collectors.toList());
    }

    @Transactional
    public void islemSil(Long islemId, Kullanici kullanici) {
        Islem islem = islemRepository.findById(islemId)
                .orElseThrow(() -> new ResourceNotFoundException("Islem", islemId));

        if (!islem.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu islemi silme yetkiniz yok.");
        }

        islemRepository.delete(islem);
    }

    @Transactional(readOnly = true)
    public OzetYaniti aylikOzetGetir(Kullanici kullanici, int yil, int ay) {
        BigDecimal toplamGelir = islemRepository.findAylikToplamGelir(kullanici.getId(), yil, ay);
        BigDecimal toplamGider = islemRepository.findAylikToplamGider(kullanici.getId(), yil, ay);
        BigDecimal netBakiye = toplamGelir.subtract(toplamGider);

        List<Object[]> kategoriVerileri = islemRepository
                .findAylikKategoriHarcamalari(kullanici.getId(), yil, ay);

        Map<String, BigDecimal> kategoriHarcamalari = new LinkedHashMap<>();
        for (Object[] veri : kategoriVerileri) {
            kategoriHarcamalari.put((String) veri[0], (BigDecimal) veri[1]);
        }

        return OzetYaniti.builder()
                .yil(yil)
                .ay(ay)
                .toplamGelir(toplamGelir)
                .toplamGider(toplamGider)
                .netBakiye(netBakiye)
                .kategoriHarcamalari(kategoriHarcamalari)
                .build();
    }

    private IslemYaniti entitydenYanitadonustur(Islem islem) {
        return IslemYaniti.builder()
                .id(islem.getId())
                .kategoriId(islem.getKategori().getId())
                .kategoriIsim(islem.getKategori().getIsim())
                .kategoriTip(islem.getKategori().getTip().name())
                .miktar(islem.getMiktar())
                .aciklama(islem.getAciklama())
                .islemTarihi(islem.getIslemTarihi())
                .build();
    }
}
