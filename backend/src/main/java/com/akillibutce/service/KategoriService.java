package com.akillibutce.service;

import com.akillibutce.dto.KategoriIstegi;
import com.akillibutce.dto.KategoriYaniti;
import com.akillibutce.entity.Kategori;
import com.akillibutce.entity.Kullanici;
import com.akillibutce.exception.ResourceNotFoundException;
import com.akillibutce.repository.KategoriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KategoriService {

    private final KategoriRepository kategoriRepository;

    @Transactional(readOnly = true)
    public List<KategoriYaniti> kullanicininKategorileriGetir(Kullanici kullanici) {
        List<Kategori> kategoriler = kategoriRepository
                .findSistemVeKullaniciKategorileri(kullanici.getId());
        return kategoriler.stream()
                .map(this::entitydenYanitadonustur)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KategoriYaniti> sistemKategorileriGetir() {
        return kategoriRepository.findByKullaniciIsNull().stream()
                .map(this::entitydenYanitadonustur)
                .collect(Collectors.toList());
    }

    @Transactional
    public KategoriYaniti ozelKategoriEkle(KategoriIstegi istek, Kullanici kullanici) {
        Kategori yeniKategori = Kategori.builder()
                .isim(istek.getIsim())
                .tip(istek.getTip())
                .zorunluMu(istek.getZorunluMu() != null ? istek.getZorunluMu() : false)
                .kullanici(kullanici)
                .build();

        Kategori kaydedilen = kategoriRepository.save(yeniKategori);
        return entitydenYanitadonustur(kaydedilen);
    }

    @Transactional
    public void kategoriSil(Long kategoriId, Kullanici kullanici) {
        Kategori kategori = kategoriRepository.findById(kategoriId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", kategoriId));

        if (kategori.isSistemKategorisi()) {
            throw new IllegalArgumentException("Sistem kategorileri silinemez.");
        }

        if (!kategori.getKullanici().getId().equals(kullanici.getId())) {
            throw new IllegalArgumentException("Bu kategoriyi silme yetkiniz yok.");
        }

        kategoriRepository.delete(kategori);
    }

    public Kategori idyeGoreGetir(Long id) {
        return kategoriRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", id));
    }

    private KategoriYaniti entitydenYanitadonustur(Kategori kategori) {
        return KategoriYaniti.builder()
                .id(kategori.getId())
                .isim(kategori.getIsim())
                .tip(kategori.getTip().name())
                .zorunluMu(kategori.getZorunluMu())
                .sistemKategorisi(kategori.isSistemKategorisi())
                .build();
    }
}
