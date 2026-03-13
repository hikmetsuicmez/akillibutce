package com.akillibutce.service;

import com.akillibutce.dto.BirikimHedefiIstegi;
import com.akillibutce.dto.BirikimHedefiYaniti;
import com.akillibutce.entity.BirikimDurum;
import com.akillibutce.entity.BirikimHedefi;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.exception.ResourceNotFoundException;
import com.akillibutce.repository.BirikimHedefiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BirikimHedefiService {

    private static final int UCRETSIZ_HEDEF_LIMITI = 1;

    private final BirikimHedefiRepository birikimHedefiRepository;

    @Transactional
    public BirikimHedefiYaniti hedefOlustur(BirikimHedefiIstegi istek, Kullanici kullanici) {
        if (!kullanici.isPremium()) {
            long aktifHedefSayisi = birikimHedefiRepository
                    .countByKullaniciIdAndDurum(kullanici.getId(), BirikimDurum.DEVAM_EDIYOR);
            if (aktifHedefSayisi >= UCRETSIZ_HEDEF_LIMITI) {
                throw new IllegalStateException(
                        "Ucretsiz kullanicilar en fazla " + UCRETSIZ_HEDEF_LIMITI +
                        " aktif hedef olusturabilir. Premium'a gecin!");
            }
        }

        BirikimHedefi hedef = BirikimHedefi.builder()
                .kullanici(kullanici)
                .baslik(istek.baslik())
                .hedefTutar(istek.hedefTutar())
                .sonTarih(istek.sonTarih())
                .build();

        return entitydenYanitaDonustur(birikimHedefiRepository.save(hedef));
    }

    @Transactional
    public BirikimHedefiYaniti paraEkle(Long hedefId, BigDecimal miktar, Kullanici kullanici) {
        BirikimHedefi hedef = hedefiGetirVeYetkilendir(hedefId, kullanici);

        if (hedef.getDurum() == BirikimDurum.TAMAMLANDI) {
            throw new IllegalStateException("Bu hedef zaten tamamlandi.");
        }

        BigDecimal yeniMevcut = hedef.getMevcutTutar().add(miktar);
        hedef.setMevcutTutar(yeniMevcut);

        if (yeniMevcut.compareTo(hedef.getHedefTutar()) >= 0) {
            hedef.setDurum(BirikimDurum.TAMAMLANDI);
        }

        return entitydenYanitaDonustur(birikimHedefiRepository.save(hedef));
    }

    @Transactional(readOnly = true)
    public List<BirikimHedefiYaniti> hedefleriGetir(Kullanici kullanici) {
        return birikimHedefiRepository
                .findByKullaniciIdOrderByOlusturulmaTarihiDesc(kullanici.getId())
                .stream()
                .map(this::entitydenYanitaDonustur)
                .collect(Collectors.toList());
    }

    @Transactional
    public void hedefSil(Long hedefId, Kullanici kullanici) {
        BirikimHedefi hedef = hedefiGetirVeYetkilendir(hedefId, kullanici);
        birikimHedefiRepository.delete(hedef);
    }

    private BirikimHedefi hedefiGetirVeYetkilendir(Long hedefId, Kullanici kullanici) {
        BirikimHedefi hedef = birikimHedefiRepository.findById(hedefId)
                .orElseThrow(() -> new ResourceNotFoundException("BirikimHedefi", hedefId));
        if (!hedef.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu hedefe erisim yetkiniz yok.");
        }
        return hedef;
    }

    private BirikimHedefiYaniti entitydenYanitaDonustur(BirikimHedefi hedef) {
        double yuzde = 0.0;
        if (hedef.getHedefTutar().compareTo(BigDecimal.ZERO) > 0) {
            yuzde = hedef.getMevcutTutar()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(hedef.getHedefTutar(), 2, RoundingMode.HALF_UP)
                    .doubleValue();
            yuzde = Math.min(yuzde, 100.0);
        }
        return new BirikimHedefiYaniti(
                hedef.getId(),
                hedef.getBaslik(),
                hedef.getHedefTutar(),
                hedef.getMevcutTutar(),
                yuzde,
                hedef.getSonTarih(),
                hedef.getDurum().name()
        );
    }
}
