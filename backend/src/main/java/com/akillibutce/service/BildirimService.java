package com.akillibutce.service;

import com.akillibutce.dto.BildirimYaniti;
import com.akillibutce.entity.Bildirim;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.BildirimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BildirimService {

    private final BildirimRepository bildirimRepository;

    @Transactional
    public void bildirimOlustur(Kullanici kullanici, String mesaj) {
        Bildirim bildirim = Bildirim.builder()
                .kullanici(kullanici)
                .mesaj(mesaj)
                .build();
        bildirimRepository.save(bildirim);
    }

    @Transactional(readOnly = true)
    public List<BildirimYaniti> bildirimleriGetir(Kullanici kullanici) {
        return bildirimRepository
                .findByKullaniciIdOrderByOlusturulmaTarihiDesc(kullanici.getId())
                .stream()
                .map(b -> new BildirimYaniti(b.getId(), b.getMesaj(), b.isOkunduMu(), b.getOlusturulmaTarihi()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long okunmamisSayisi(Kullanici kullanici) {
        return bildirimRepository.countByKullaniciIdAndOkunduMuFalse(kullanici.getId());
    }

    @Transactional
    public void tumunuOkunduIsaretle(Kullanici kullanici) {
        List<Bildirim> bildirimler = bildirimRepository
                .findByKullaniciIdOrderByOlusturulmaTarihiDesc(kullanici.getId());
        bildirimler.stream()
                .filter(b -> !b.isOkunduMu())
                .forEach(b -> b.setOkunduMu(true));
        bildirimRepository.saveAll(bildirimler);
    }
}
