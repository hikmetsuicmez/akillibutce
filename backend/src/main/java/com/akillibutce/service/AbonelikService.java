package com.akillibutce.service;

import com.akillibutce.entity.*;
import com.akillibutce.repository.AbonelikRepository;
import com.akillibutce.repository.KullaniciRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AbonelikService {

    private final AbonelikRepository abonelikRepository;
    private final KullaniciRepository kullaniciRepository;

    @Transactional
    public void premiumeYukselt(Kullanici kullanici, AbonelikTip tip) {
        // Mevcut aktif aboneligi pasife al
        abonelikRepository.findByKullaniciIdAndDurum(kullanici.getId(), AbonelikDurum.AKTIF)
                .ifPresent(abonelik -> {
                    abonelik.setDurum(AbonelikDurum.PASIF);
                    abonelikRepository.save(abonelik);
                });

        LocalDate baslangic = LocalDate.now();
        LocalDate bitis = (tip == AbonelikTip.YILLIK)
                ? baslangic.plusYears(1)
                : baslangic.plusMonths(1);

        Abonelik yeniAbonelik = Abonelik.builder()
                .kullanici(kullanici)
                .baslangicTarihi(baslangic)
                .bitisTarihi(bitis)
                .durum(AbonelikDurum.AKTIF)
                .tip(tip)
                .build();

        abonelikRepository.save(yeniAbonelik);

        kullanici.setRol(KullaniciRol.ROLE_PREMIUM_USER);
        kullaniciRepository.save(kullanici);
    }

    @Transactional
    public void aboneligiIptalEt(Kullanici kullanici) {
        abonelikRepository.findByKullaniciIdAndDurum(kullanici.getId(), AbonelikDurum.AKTIF)
                .ifPresent(abonelik -> {
                    abonelik.setDurum(AbonelikDurum.PASIF);
                    abonelikRepository.save(abonelik);
                });

        kullanici.setRol(KullaniciRol.ROLE_FREE_USER);
        kullaniciRepository.save(kullanici);
    }

    @Transactional(readOnly = true)
    public boolean aktifAbonelikVarMi(Long kullaniciId) {
        return abonelikRepository.findByKullaniciIdAndDurum(kullaniciId, AbonelikDurum.AKTIF)
                .map(Abonelik::isAktif)
                .orElse(false);
    }
}
